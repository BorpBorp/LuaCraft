package com.luacraft.sandbox.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class ItemStackFactory extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue material, LuaValue amount) {
        Material mat = Material.matchMaterial(material.checkjstring());
        int amt = amount.checkint();
        
        if (mat == null) return LuaValue.NIL;

        ItemStack stack = new ItemStack(mat, amt);
        return new ItemStackLib(stack);
    }
}