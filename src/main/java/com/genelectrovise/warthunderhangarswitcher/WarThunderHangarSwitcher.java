package com.genelectrovise.warthunderhangarswitcher;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarThunderHangarSwitcher {

    private static String warThunderLoc = "";
    private static List validCommands = Stream.of(new String[]{"switch", "list"}).collect(Collectors.toList());

    public static void main(String[] args) throws IOException {

        Thread.currentThread().setUncaughtExceptionHandler(new WTHSExceptionHandler());

        log("War Thunder Hangar Switcher, by GenElectrovise");

        if (args.length < 1)
            throw new IllegalStateException("Must have at least 1 argument! Commands: [switch], [list]");
        if (!validCommands.contains(args[0]))
            throw new IllegalStateException(args[0] + " is not a valid command. Valid commands are [switch], [list]");

        warThunderLoc = getWarThunderLoc();

        if (args[0].equals("switch")) {
            if (args.length < 2)
                throw new IllegalStateException("Switch command requires an index. Example: [switch 1]. Use [list] to get hangar indices.");
            switchHangar(args[1]);
        }

        if (args[0].equals("list")) {
            listHangars();
        }
    }

    private static void listHangars() {

        log("Listing hangars:", false);

        File hangarsFile = new File(warThunderLoc + "/custom_hangars");
        if (!hangarsFile.exists()) hangarsFile.mkdir();

        List<File> hangars = Stream.of(hangarsFile.listFiles((name -> name.getAbsolutePath().endsWith(".blk")))).collect(Collectors.toList());

        for (File file : hangars)
            log(hangars.indexOf(file) + " - " + file.getName(), false);

        log("");

    }

    private static void switchHangar(String arg1) throws IOException {
        log("Switching hangars");

        File configBlk = new File(warThunderLoc + "/config.blk");
        List<String> lines = new BufferedReader(new FileReader(configBlk)).lines().collect(Collectors.toList());

        String originalBlkLine = "";
        for (String line : lines) {
            if (line.startsWith("hangarBlk:t=")) originalBlkLine = line;
        }
        if (originalBlkLine == "") {
            log("No hangarBlk:t= exists in the given config file.");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configBlk, true))) {
                writer.newLine();
                writer.append("hangarBlk:t=\"\"");
                writer.newLine();
                writer.flush();
            }
            log("hangarBlk:t=\"\" has been appended to the end of your config.blk file " + configBlk.getAbsolutePath());
        } else {
            log("If you want to revert, or an error occurs, your current hangarBlk line is " + originalBlkLine);
        }

        File hangarsFile = new File(warThunderLoc + "/custom_hangars");
        if (!hangarsFile.exists()) hangarsFile.mkdir();
        List<File> hangars = Stream.of(hangarsFile.listFiles((name -> name.getAbsolutePath().endsWith(".blk")))).collect(Collectors.toList());

        int index;
        try {
            index = Integer.valueOf(arg1);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("[switch] requires an integer argument, for example [switch 3]. Use [list] for hangar indices. The given argument " + arg1 + " is not an integer.");
        }
        if (index < 0 || index > hangars.size() - 1)
            throw new IndexOutOfBoundsException("Provided index " + index + " is out of the bounds 0-" + (hangars.size() - 1) + ". Use [list] to find hangar indices.");

        String newHangar = "custom_hangars/" + hangars.get(index).getName();

        replaceLines(configBlk, newHangar);

        log("Your hangar is now: " + newHangar);
    }

    private static String getWarThunderLoc() throws IOException {

        File configFile = new File(System.getProperty("user.dir") + "/config.wths");
        if (!configFile.exists()) {
            log("config file " + configFile.getAbsolutePath() + " does not exist - creating");
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.append("warThunderLoc=");
                writer.flush();
            }
        }

        log("Found config file " + configFile.getAbsolutePath());

        List<String> lines = new BufferedReader(new FileReader(configFile)).lines().collect(Collectors.toList());

        for (String line : lines) {
            if (line.startsWith("warThunderLoc=")) ;
            String loc = line.replace("warThunderLoc=", "");

            if (loc.equals(""))
                throw new NullPointerException("Found config.wths 'warThunderLoc=' but location not specified");
            if (!new File(loc).exists())
                throw new FileNotFoundException("Could not find warThunderLoc " + loc + " (does not exist)");

            log("Using warThunderLoc: " + loc);
            return loc;
        }

        throw new NullPointerException("War thunder location not specified in wths_config, use warThunderLoc=LOCATION (e.g. C:/War Thunder)");
    }

    public static void log(String msg) {
        log(msg, true);
    }

    public static void log(String msg, boolean thenReturn) {
        System.out.println(" > " + msg + (thenReturn ? System.lineSeparator() : ""));
    }

    public static void err(String msg) {
        System.err.println(" > " + msg);
    }

    // read file one line at a time
    // replace line as you read the file and store updated lines in StringBuffer
    // overwrite the file with the new lines
    public static void replaceLines(File input, String replaceWith) {
        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(input));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                if (line.startsWith("hangarBlk:t=")) {
                    line = "hangarBlk:t=\"" + replaceWith + "\"";
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(input);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }
}
