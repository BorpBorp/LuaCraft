<img width="1536" height="355" alt="image" src="https://github.com/user-attachments/assets/8a7e449b-1991-4b13-9891-5bf3ff9097fd" />


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
LuaCraft depends on `Vault`
# Download
You can download the latest release of LuaCraft at the [Releases](https://github.com/BorpBorp/LuaCraft/releases)
# Example
```lua
function ServerEvent.OnPlayerJoin(event)
   local player = event.player
   local white = "&#FFFFFF"
   local component = Component(white, "Hello world")

   Chat.Broadcast(component)
end
```
