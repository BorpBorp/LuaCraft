package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.block.BlockLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.inventory.EquipmentSlotLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class PlayerBlockPlace implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerBlockPlace(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        for (Globals globals : allGlobals.values()) {
            LuaValue eventsTable = globals.get("ServerEvent");
            LuaValue function = eventsTable.get("onBlockPlace");

            LuaFunction canBuild = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.canBuild());
                }
            };

            LuaFunction getBlockAgainst = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new BlockLib(event.getBlockAgainst());
                }
            };

            LuaFunction getBlockPlaced = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new BlockLib(event.getBlockPlaced());
                }
            };

            LuaFunction getHand = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new EquipmentSlotLib(event.getHand());
                }
            };

            LuaFunction getItemInHand = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getItemInHand());
                }
            };

            LuaFunction setBuild = new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue canBuild) {
                    if (canBuild.isboolean() || canBuild.isnil()) {
                        event.setBuild(canBuild.toboolean());
                    } else {
                        throw new LuaError("SetBuild requires a boolean argument");
                    }

                    return LuaValue.NIL;
                }
            };

            LuaFunction canPlace = new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue place) {
                    if (place.isboolean() || place.isnil()) {
                        event.setCancelled(place.toboolean());
                    } else {
                        throw new LuaError("CanPlace requires a boolean argument");
                    }

                    return LuaValue.NIL;
                }
            };

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("CanBuild", canBuild);
            luaEvent.set("GetBlockAgainst", getBlockAgainst);
            luaEvent.set("GetBlockPlaced", getBlockPlaced);
            luaEvent.set("GetHand", getHand);
            luaEvent.set("GetItemInHand", getItemInHand);
            luaEvent.set("SetBuild", setBuild);
            luaEvent.set("CanPlace", canPlace);
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
