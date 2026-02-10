package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.entity.PlayerLib;

import net.kyori.adventure.text.Component;

public class PlayerQuit implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerQuit(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (Globals globals : allGlobals.values()) {
            LuaValue eventsTable = globals.get("ServerEvent");
            LuaValue function = eventsTable.get("onPlayerQuit");

            LuaFunction setQuitMessage = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    if (arg.isnil()) {
                        event.quitMessage(null);
                    } else {
                        Component message = (Component) arg.checkuserdata(Component.class);
                        event.quitMessage(message);
                    }
                    return LuaValue.NIL;
                }
            };

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("raw", CoerceJavaToLua.coerce(event));
            luaEvent.set("setQuitMessage", setQuitMessage);
            luaEvent.set("player", new PlayerLib(player));
            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(CoerceJavaToLua.coerce(luaEvent));
                } catch(LuaError e) {
                    Bukkit.getLogger().info("Lua Script Error: " + e.getMessage());
                }
            }
        }
    }
}
