package com.genelectrovise.warthunderhangarswitcher;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static String warThunderLoc = "";

    public static void main(String[] args) throws IOException {

        warThunderLoc = getWarThunderLoc();

        log("Received argument 0: " + args[0]);

        if (args[0].equals("switch")) {
            if (args.length < 2)
                throw new IndexOutOfBoundsException("Switch command requires an index. Example: 'switch 1'. Use 'list' to get hangar indices.");
            switchHangar(args[1]);
        }

        if (args[0].equals("list")) {
            listHangars();
        }
    }

    private static void listHangars() {

        log("Listing hangars");
        log("");

        File hangarsFile = new File(warThunderLoc + "/custom_hangars");
        if (!hangarsFile.exists()) hangarsFile.mkdir();

        List<File> hangars = List.of(hangarsFile.listFiles((name -> name.getAbsolutePath().endsWith(".blk"))));
        for (File file : hangars)
            log(hangars.indexOf(file) + " - " + file.getName());

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
        List<File> hangars = List.of(hangarsFile.listFiles((name -> name.getAbsolutePath().endsWith(".blk"))));
        String newHangar = "custom_hangars/" + hangars.get(Integer.valueOf(arg1)).getName();

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
            log("Using warThunderLoc: " + loc);
            return loc;
        }

        throw new NullPointerException("War thunder location not specified in wths_config, use warThunderLoc=LOCATION (e.g. C:/War Thunder)");
    }

    public static void log(String msg) {
        System.out.println(msg);
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
