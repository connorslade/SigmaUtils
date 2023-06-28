# SigmaUtils ![build](https://github.com/Basicprogrammer10/SigmaUtils/actions/workflows/build.yml/badge.svg) ![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Basicprogrammer10/SigmaUtils?include_prereleases) ![GitHub all releases](https://img.shields.io/github/downloads/Basicprogrammer10/SigmaUtils/total)

Requires: <kbd>[Minecraft 1.20.1](https://minecraft.fandom.com/wiki/Java_Edition_1.20.1)</kbd> <kbd>[Fabric API](https://modrinth.com/mod/fabric-api/version/0.75.3+1.19.4)</kbd>

SigmaUtils is a utility mod for Minecraft 1.19.4.
It is made up of 'modules', which are discrete features that can be toggled and configured.
By default, SigmaUtils changes nothing about the game, except adding a button to the title screen to open the config
GUI.
To see all the modules you can check out
the [module documentation](https://github.com/Basicprogrammer10/SigmaUtils/wiki/Modules).

## Usage

You can access the module config screen by clicking the <kbd>Σ</kbd> button on the main screen or by pressing <kbd>u</kbd> (default) in game.
When in the config screen you can toggle modules on and off by left-clicking on them.
When your mouse is over a module you can see a description of what it does.
When you right-click a module, it will open a config screen for that module.
All modules have two base settings, `Enabled` and `Keybind`, but some modules have more.
Just like with the modules, hovering over any setting will show a description of what it does.

There are also commands that you can use in game (about, chat, fotd, map, note, resourcepack, run, save, task, toggle).
All the commands are accessible by through the base `/util` command.
For more information on the commands you can check out the [command documentation](https://github.com/Basicprogrammer10/SigmaUtils/wiki/Commands).

### Example Use Cases

- When making time-lapse videos (with or without replay mod) you can use [Force Weather](https://github.com/Basicprogrammer10/SigmaUtils/wiki/Misc#forceweather) and [Force Game Time](https://github.com/Basicprogrammer10/SigmaUtils/wiki/Misc#forcegametime) to get a more constant look
- If playing on servers like Hypixel you can use [Badlion Timers](https://github.com/Basicprogrammer10/SigmaUtils/wiki/Server#badliontimers) to show server defined countdown timers as a HUD element ([image](https://user-images.githubusercontent.com/50306817/215306367-3b2b640c-898e-401b-8ee2-c97e7879e4c8.png))
- If you just want to see some interesting stats you can enable [Player History](https://github.com/Basicprogrammer10/SigmaUtils/wiki/Server#playerhistory), which logs every player is in the same server as you. You can then see which players you have seen before and how many times.
- You can use the [`/util map save`](https://github.com/Basicprogrammer10/SigmaUtils/wiki/Commands#map) command to download map art from a server into your screenshots folder.
