package com.luacraft.sandbox.entity;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.inventory.EquipmentLib;
import com.luacraft.sandbox.inventory.InventoryLib;
import com.luacraft.sandbox.location.LocationLib;

public class EntityLib extends LuaTable {
    public EntityLib(LivingEntity entity) {
        rawset(LuaValue.valueOf("GetHealth"), new ZeroArgFunction() {
            public LuaValue call() {
                return LuaValue.valueOf(entity.getHealth());
            }
        });

        rawset(LuaValue.valueOf("SetHealth"), new OneArgFunction() {
            public LuaValue call(LuaValue health) {
                double newHealth = health.todouble();

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

        rawset(LuaValue.valueOf("GetLocation"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(entity.getLocation());
            }
        });

        rawset(LuaValue.valueOf("Teleport"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue location) {
                Location newLocation = ((LocationLib) location).getLocation();

                entity.teleport(newLocation);

                return LuaValue.NIL;
            }
        });
    }
}
