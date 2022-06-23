# War Thunder Hangar Switcher, by GenElectrovise

Releases can be found on this GitHub page, under releases.

Run the .exe or .jar from the command line: `WarThunderHangarSwitcher_1.0.exe`

The permitted commands are `switch` and `list`.

The .exe is just the .jar packaged with a built in JRE using Launch4J (Launch4J config in source root)

## config.wths

It should be in the same directory as the .exe or .jar is running in.

The contents should look something like: `warThunderLoc=C:\Users\your_name\Program Files\Steam\steamapps\common\WarThunder`

Where the path after "warThunderLoc=" is the path to wherever your War Thunder installation is.

This file will be automatically generated with the contents "warThunderLoc=".

## Commands

## List

`WarThunderHangarSwitcher_1.0.exe list`

This will result in a list looking something like:
 > Listing hangars:
 > 0 - hangar.blk
 > 1 - another_hangar.blk
 > 2 - and_another_hangar.blk

The numbers on the left of each name is the index of the hangar. You will need this for the switch command.

## Switch

`WarThunderHangarSwitcher_1.0.exe switch INDEX`

Switches to the hangar of the given index, for example `WarThunderHangarSwitcher_1.0.exe switch 1` will switch to the hangar of index 1 (in the above list that would be "another_hangar.blk")