package com.luacraft.sandbox.inventory;

import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.item.ItemStackLib;

public class EquipmentLib extends LuaTable {
    public EquipmentLib(EntityEquipment equipment) {
        rawset(LuaValue.valueOf("SetMainHand"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                
                if (arg.isnil()) {
                    equipment.setItemInMainHand(null);
                } else {
                    ItemStack item = ((ItemStackLib) arg).stack;
                    equipment.setItemInMainHand(item);
                }
                
                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetOffHand"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {

                if (arg.isnil()) {
                    equipment.setItemInOffHand(null);
                } else {
                    ItemStack item = ((ItemStackLib) arg).stack;
                    equipment.setItemInOffHand(item);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetHelmet"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {

                if (arg.isnil()) {
                    equipment.setHelmet(null);
                } else {
                    ItemStack item = ((ItemStackLib) arg).stack;
                    equipment.setHelmet(item);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetChestplate"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {

                if (arg.isnil()) {
                    equipment.setChestplate(null);
                } else {
                    ItemStack item = ((ItemStackLib) arg).stack;
                    equipment.setChestplate(item);
                }

                return LuaValue.NIL;
            } 
        });

        rawset(LuaValue.valueOf("SetLeggings"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {

                if (arg.isnil()) {
                    equipment.setChestplate(null);
                } else {
                    ItemStack item = ((ItemStackLib) arg).stack;
                    equipment.setLeggings(item);
                }

                return LuaValue.NIL;
            } 
        });

        rawset(LuaValue.valueOf("SetBoots"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {

                if (arg.isnil()) {
                    equipment.setBoots(null);
                } else {
                    ItemStack item = ((ItemStackLib) arg).stack;
                    equipment.setBoots(item);
                }

                return LuaValue.NIL;
            }
        });
    }
}
