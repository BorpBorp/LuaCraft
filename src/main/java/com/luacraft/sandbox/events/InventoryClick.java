package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.inventory.InventoryLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class InventoryClick implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public InventoryClick(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        for (Globals globals : allGlobals.values()) {
            LuaValue serverEvent = globals.get("ServerEvent");
            LuaValue function = serverEvent.get("OnInventoryClick");

            LuaFunction getClickType = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getClick().toString());
                }
            };

            LuaFunction getClickedInventory = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new InventoryLib(event.getClickedInventory());
                }
            };

            LuaFunction getSlotItem = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getCurrentItem());
                }
            };

            LuaFunction getCursorItem = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getCursor());
                }
            };

            LuaFunction getHotbarButton = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getHotbarButton());
                }
            };

            LuaFunction getSlot = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getSlot());
                }
            };

            LuaFunction getTitle = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ComponentLib(event.getView().title());
                }
            };

            LuaFunction isLeftClick = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isLeftClick());
                }
            };

            LuaFunction isRightClick = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isRightClick());
                }
            };

            LuaFunction isShiftClick = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isShiftClick());
                }
            };

            LuaFunction shouldInteract = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!should.toboolean());

                    return LuaValue.NIL;
                }
            };

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("GetClickType", getClickType);
            luaEvent.set("GetClickedInventory", getClickedInventory);
            luaEvent.set("GetSlotItem", getSlotItem);
            luaEvent.set("GetCursorItem", getCursorItem);
            luaEvent.set("GetHotbarButton", getHotbarButton);
            luaEvent.set("GetSlot", getSlot);
            luaEvent.set("GetTitle", getTitle);
            luaEvent.set("IsLeftclick", isLeftClick);
            luaEvent.set("IsRightClick", isRightClick);
            luaEvent.set("IsShiftClick", isShiftClick);
            luaEvent.set("ShouldInteract", shouldInteract);
            luaEvent.set("Inventory", new InventoryLib(event.getInventory()));
            luaEvent.set("Player", new PlayerLib(player));
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
