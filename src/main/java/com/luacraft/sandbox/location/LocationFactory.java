package com.luacraft.sandbox.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LocationFactory extends VarArgFunction {
    @Override
    public Varargs invoke(Varargs args) {
        if (args.narg() != 4) {
            throw new LuaError("Location() requires 4 arguments (x, y, z, world)");
        }

        double x = args.checkdouble(1);
        double y = args.checkdouble(2);
        double z = args.checkdouble(3);
        String worldName = args.checkjstring(4);
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new LuaError("World '" + worldName + "' does not exist or is not loaded");
        }

        Location location = new Location(world, x, y, z);

        return new LocationLib(location);
    }
}