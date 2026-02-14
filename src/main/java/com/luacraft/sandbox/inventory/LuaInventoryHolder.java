package com.luacraft.sandbox.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.luaj.vm2.LuaTable;

public class LuaInventoryHolder implements InventoryHolder {
    private Inventory inventory;
    private final LuaTable scriptData;

    public LuaInventoryHolder(LuaTable scriptData) {
        this.scriptData = scriptData;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public LuaTable getScriptData() {
        return scriptData;
    }
}
