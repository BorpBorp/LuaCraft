package com.luacraft.sandbox.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.item.ItemStackLib;

public class InventoryLib extends LuaTable {
    public InventoryLib(Inventory inventory) {

        rawset(LuaValue.valueOf("GiveItem"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (inventory != null) {
                    ItemStack item = ((ItemStackLib) arg).stack;
                    inventory.addItem(item);
                }

                return LuaValue.NIL;
            }
        });
    }
}