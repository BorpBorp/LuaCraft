package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
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
import com.luacraft.sandbox.location.LocationLib;

public class PlayerInteract implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerInteract(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        for (Globals globals : allGlobals.values()) {
            LuaValue eventsTable = globals.get("ServerEvent");
            LuaValue function = eventsTable.get("OnPlayerInteract");
            
            LuaFunction getActionType = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getAction().toString());
                }
            };

            LuaFunction getClickedBlock = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new BlockLib(event.getClickedBlock());
                }
            };

            LuaFunction getHand = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new EquipmentSlotLib(event.getHand());
                }
            };

            LuaFunction getInteractionPoint = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new LocationLib(event.getInteractionPoint());
                }
            };

            LuaFunction getItem = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getItem());
                }
            };

            LuaFunction involvedBlock = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasBlock());
                }
            };

            LuaFunction involvedItem = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasItem());
                }
            };

            LuaFunction isBlockInHand = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isBlockInHand());
                }
            };

            LuaFunction shouldInteract = new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!should.toboolean());

                    return LuaValue.NIL;
                }
            };

            LuaFunction shouldUseInteractedBlock = new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    String can = "DEFAULT";

                    if (should.toboolean()) {
                        can = "ALLOW";
                    } else {
                        can = "DENY";
                    }

                    Event.Result result = Event.Result.valueOf(can);

                    event.setUseInteractedBlock(result);

                    return LuaValue.NIL;
                }
            };

            LuaFunction shouldUseItemInHand = new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    String can = "DEFAULT";

                    if (should.toboolean()) {
                        can = "ALLOW";
                    } else {
                        can = "DENY";
                    }

                    Event.Result result = Event.Result.valueOf(can);

                    event.setUseItemInHand(result);

                    return LuaValue.NIL;
                }
            };
            
            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("GetActionType", getActionType);
            luaEvent.set("GetClickedBlock", getClickedBlock);
            luaEvent.set("GetHand", getHand);
            luaEvent.set("GetInteractionPoint", getInteractionPoint);
            luaEvent.set("GetItem", getItem);
            luaEvent.set("InvolvedBlock", involvedBlock);
            luaEvent.set("InvolvedItem", involvedItem);
            luaEvent.set("IsBlockInHand", isBlockInHand);
            luaEvent.set("ShouldInteract", shouldInteract);
            luaEvent.set("ShouldUseInteractedBlock", shouldUseInteractedBlock);
            luaEvent.set("ShouldUseItemInHand", shouldUseItemInHand);

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
