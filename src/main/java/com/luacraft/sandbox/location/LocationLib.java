package com.luacraft.sandbox.location;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.entity.ItemLib;
import com.luacraft.sandbox.item.ItemStackLib;

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
            public LuaValue call(LuaValue loc) {
                if (loc instanceof LocationLib) {
                    Location otherLoc = ((LocationLib) loc).getLocation();

                    return LuaValue.valueOf(location.distance(otherLoc));
                }
                throw new LuaError("DistanceTo requires a Location object");
            }
        });

        rawset(LuaValue.valueOf("DistanceToSquared"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue loc) {
                if (loc instanceof LocationLib) {
                    Location otherLoc = ((LocationLib) loc).getLocation();
                    
                    return LuaValue.valueOf(location.distanceSquared(otherLoc));
                }
                throw new LuaError("DistanceToSquared requires a Location object");
            }
        });

        rawset(LuaValue.valueOf("IsWithinBounds"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue loc1, LuaValue loc2) {
                if (loc1 instanceof LocationLib && loc2 instanceof LocationLib) {
                    Location location1 = ((LocationLib) loc1).getLocation();
                    Location location2 = ((LocationLib) loc2).getLocation();

                    double minX = Math.min(location1.getX(), location2.getX());
                    double maxX = Math.max(location1.getX(), location2.getX());
                    
                    double minY = Math.min(location1.getY(), location2.getY());
                    double maxY = Math.max(location1.getY(), location2.getY());
                    
                    double minZ = Math.min(location1.getZ(), location2.getZ());
                    double maxZ = Math.max(location1.getZ(), location2.getZ());
                    
                    boolean inRegion = location.getX() >= minX && location.getX() <= maxX && location.getY() >= minY && location.getY() <= maxY && location.getZ() >= minZ && location.getZ() <= maxZ;
                    
                    return LuaValue.valueOf(inRegion);
                }
                throw new LuaError("IsInRegion requires two Location objects");
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

        rawset(LuaValue.valueOf("DropItem"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue itemstack) {
                ItemStack stack = ((ItemStackLib) itemstack).getItemStack();

                Item droppedItem = location.getWorld().dropItem(location, stack);

                return new ItemLib(droppedItem);
            }
        });

        rawset(LuaValue.valueOf("Compare"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue loc) {
                Location otherLoc = ((LocationLib) loc).getLocation();

                return LuaValue.valueOf(location.equals(otherLoc));
            }
        });
    }

    public Location getLocation() {
        return location;
    }
}
