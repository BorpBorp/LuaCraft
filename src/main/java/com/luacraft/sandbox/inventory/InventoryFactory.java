package com.luacraft.sandbox.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import com.luacraft.sandbox.util.ComponentUtils;

public class InventoryFactory extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue rows, LuaValue title) {
        LuaTable meta = new LuaTable();
        LuaInventoryHolder holder = new LuaInventoryHolder(meta);
        Integer size = rows.checkint() * 9;

        Inventory inv = Bukkit.createInventory(holder, size, ComponentUtils.luaValueToComponent(title));

        holder.setInventory(inv);

        return new InventoryLib(inv);
    }
}