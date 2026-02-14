package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.block.BlockLib;
import com.luacraft.sandbox.entity.PlayerLib;

public class PlayerBlockBreak implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerBlockBreak(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        for (Globals globals : allGlobals.values()) {
            LuaValue serverEvent = globals.get("ServerEvent");
            LuaValue function = serverEvent.get("OnBlockBreak");

            LuaFunction shouldBreak = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    if (arg.isboolean() || arg.isnil()) {
                        event.setCancelled(!arg.toboolean());
                    }

                    return LuaValue.NIL;
                }
            };

            LuaFunction shouldDropItems = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    if (arg.isboolean() || arg.isnil()) {
                        event.setDropItems(arg.toboolean());
                    }

                    return LuaValue.NIL;
                }
            };

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("ShouldBreak", shouldBreak);
            luaEvent.set("ShouldDropItems", shouldDropItems);
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("Block", new BlockLib(block));
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
