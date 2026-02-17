package com.luacraft.sandbox.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.velocity.VectorLib;

public class EntityLib extends LuaTable {
    private final Entity entity;

    public EntityLib(Entity entity) {
        this.entity = entity;
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

        rawset(LuaValue.valueOf("SetGlow"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue glow) {
                entity.setGlowing(glow.toboolean());

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetVelocity"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue velocity) {
                Vector vel = ((VectorLib) velocity).getVector();

                entity.setVelocity(vel);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetVelocity"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new VectorLib(entity.getVelocity());
            }
        });
    }

    public Entity getEntity() {
        return entity;
    }
}