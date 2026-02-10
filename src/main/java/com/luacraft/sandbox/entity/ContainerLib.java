package com.luacraft.sandbox.entity;

import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.inventory.InventoryLib;

public class ContainerLib extends LuaTable {
    public ContainerLib(Container container) {
        rawset(LuaValue.valueOf("IsDoubleChest"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(
                    container.getInventory().getHolder() instanceof DoubleChest
                );
            } 
        });

        rawset(LuaValue.valueOf("Inventory"), new InventoryLib(null));
    }
}
