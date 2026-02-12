package com.luacraft.sandbox.location;

import org.bukkit.Location;
import org.bukkit.Material;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LocationLib extends LuaTable {
    private final Location location;
    public LocationLib(Location location) {
        this.location = location;

        rawset(LuaValue.valueOf("GetX"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(location.getX());
            }
        });
        rawset(LuaValue.valueOf("GetY"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(location.getY());
            }
        });
        rawset(LuaValue.valueOf("GetZ"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(location.getZ());
            }
        });

        rawset(LuaValue.valueOf("DistanceTo"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg instanceof LocationLib) {
                    Location otherLoc = ((LocationLib) arg).getLocation();

                    return LuaValue.valueOf(location.distance(otherLoc));
                }
                throw new org.luaj.vm2.LuaError("DistanceTo requires a Location object");
            }
        });

        rawset(LuaValue.valueOf("DistanceToSquared"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg instanceof LocationLib) {
                    Location otherLoc = ((LocationLib) arg).getLocation();
                    
                    return LuaValue.valueOf(location.distanceSquared(otherLoc));
                }
                throw new org.luaj.vm2.LuaError("DistanceToSquared requires a Location object");
            }
        });

        rawset(LuaValue.valueOf("SetBlockAtLocation"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Material material = Material.matchMaterial(arg.tojstring());

                location.getBlock().setType(material);

                return LuaValue.NIL;
            }
        });
    }

    public Location getLocation() {
        return location;
    }
}
