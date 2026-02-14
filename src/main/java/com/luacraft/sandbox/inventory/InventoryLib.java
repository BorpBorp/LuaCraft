package com.luacraft.sandbox.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class InventoryLib extends LuaTable {
    public InventoryLib(Inventory inventory) {

        rawset(LuaValue.valueOf("GiveItem"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                if (inventory != null) {
                    ItemStack i = ((ItemStackLib) item).stack;
                    inventory.addItem(i);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetItem"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue slot, LuaValue item) {
                if (inventory != null) {
                    ItemStack i = ((ItemStackLib) item).stack;
                    inventory.setItem(slot.toint(), i);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("Open"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ply) {
                Player player = null;

                if (ply instanceof PlayerLib lib) {
                    player = lib.getPlayer();
                }

                if (player != null) {
                    player.updateInventory();
                    player.openInventory(inventory);
                }
                    

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("UpdateInventory"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                for (HumanEntity viewer : inventory.getViewers()) {
                    if (viewer instanceof Player player) {
                        player.updateInventory();
                    }
                }

                return LuaValue.NIL;
            }
        });
    }
}