package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.location.LocationLib;

public class PlayerMove implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();
    
    public PlayerMove(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        for (Globals globals : allGlobals.values()) {
            LuaValue eventsTable = globals.get("ServerEvent");
            LuaValue function = eventsTable.get("onPlayerMove");

            LuaFunction shouldMove = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    if (arg.isboolean() || arg.isnil()) {
                        boolean val = arg.toboolean();

                        event.setCancelled(val);
                    } else {
                        throw new LuaError("shouldMove requires a boolean!");
                    }

                    return LuaValue.NIL;
                }
            };

            LuaFunction getLastPosition = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new LocationLib(event.getFrom());
                }
            };

            LuaFunction hasChangedBlock = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasChangedBlock());
                }
            };

            LuaFunction hasChangedOrientation = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasChangedOrientation());
                }
            };

            LuaFunction hasChangedPosition = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasChangedBlock());
                }
            };

            LuaFunction explicitHasChangedBlock = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasExplicitlyChangedBlock());
                }
            };

            LuaFunction explicitHasChangedPosition = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasExplicitlyChangedPosition());
                }
            };

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("ShouldMove", shouldMove);
            luaEvent.set("GetLastPosition", getLastPosition);
            luaEvent.set("HasChangedBlock", hasChangedBlock);
            luaEvent.set("HasChangedOrientation", hasChangedOrientation);
            luaEvent.set("HasChangedPosition", hasChangedPosition);
            luaEvent.set("ExplicitHasChangedBlock", explicitHasChangedBlock);
            luaEvent.set("ExplicitHasChangedPosition", explicitHasChangedPosition);
            luaEvent.set("Player", new PlayerLib(player));

            if (!function.isnil()) {
                try {
                    function.call(CoerceJavaToLua.coerce(luaEvent));
                } catch (LuaError e) {
                    Bukkit.getLogger().info("Lua Script Error: " + e.getMessage());
                }
            }
        }
    }
}
