package com.luacraft.sandbox.entity;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.item.ItemStackLib;

public class ItemLib extends EntityLib {
    public ItemLib(Item entity) {
        super(entity);

        rawset(LuaValue.valueOf("GetItemStack"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new ItemStackLib(entity.getItemStack());
            }
        });

        rawset(LuaValue.valueOf("SetItemStack"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue material, LuaValue amount) {
                Material mat = Material.matchMaterial(material.checkjstring());
                int amt = amount.checkint();

                if (mat == null) return LuaValue.NIL;

                ItemStack stack = new ItemStack(mat, amt);

                entity.setItemStack(stack);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetPickupDelay"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue delay) {
                int newDelay = delay.checkint();

                entity.setPickupDelay(newDelay);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetPickupDelay"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getPickupDelay());
            }
        });
    }   
}
