package com.luacraft.sandbox.inventory;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class EquipmentSlotLib extends LuaTable {
    public EquipmentSlotLib(EquipmentSlot equipmentslot) {
        rawset(LuaValue.valueOf("IsArmor"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(equipmentslot.isArmor());
            }
        });

        rawset(LuaValue.valueOf("IsHand"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(equipmentslot.isHand());
            }
        });

        rawset(LuaValue.valueOf("IsMainHand"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(equipmentslot.getGroup() == EquipmentSlotGroup.MAINHAND);
            }
        });

        rawset(LuaValue.valueOf("IsOffHand"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(equipmentslot.getGroup() == EquipmentSlotGroup.OFFHAND);
            }
        });
    }
}