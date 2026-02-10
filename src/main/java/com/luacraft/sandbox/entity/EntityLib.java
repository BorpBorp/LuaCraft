package com.luacraft.sandbox.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.inventory.EquipmentLib;
import com.luacraft.sandbox.inventory.InventoryLib;

public class EntityLib extends LuaTable {
    public EntityLib(LivingEntity entity) {
        rawset(LuaValue.valueOf("GetHealth"), new ZeroArgFunction() {
            public LuaValue call() {
                return LuaValue.valueOf(entity.getHealth());
            }
        });

        rawset(LuaValue.valueOf("SetHealth"), new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                int newHealth = arg.toint();

                entity.setHealth(newHealth);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetName"), new ZeroArgFunction() {
           public LuaValue call() {
            return LuaValue.valueOf(entity.getName());
           } 
        });

        rawset(LuaValue.valueOf("Equipment"), new EquipmentLib(entity.getEquipment()));

        if (entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            rawset(LuaValue.valueOf("Inventory"), new InventoryLib(inv));
        }

        rawset(LuaValue.valueOf("SetSaddle"), new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                if (entity instanceof Pig pig) {
                    pig.setSaddle(arg.toboolean());
                }
                
                return LuaValue.NIL;
            }
        });
    }
}
