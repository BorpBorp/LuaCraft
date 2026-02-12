# LuaCraft
LuaCraft embeds LuaJ into Paper/Bukkit servers, providing developers with a lightweight, fast scripting layer without the complexity of full plugin development.
<br>
<br>
This is the original maintained repository of LuaCraft
# Requirements
LuaCraft requires Paper to be installed on the server, it does not support Spigot
<br>
<br>
LuaCraft currently supports 1.21.4+ and will intend to support this version and forward. The current projects scope does not intend to support lower versions than this.
<br>
<br>
We unfortunately do not intend to support Spigot in the future either
# Download
You can download the latest release of LuaCraft at the [Releases](https://github.com/BorpBorp/LuaCraft/releases)
# Example
```lua
function ServerEvent.OnPlayerJoin(event)
   local player = event.player
   local red = Color(255, 0, 0)
   local component = Chat.ColoredString(red, "Hello world")

   Chat.Broadcast(component)
end
```
