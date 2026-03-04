package com.luacraft.sandbox.entity;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.location.LocationLib;

public class BlockDisplayFactory extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue loc) {
        Location location = ((LocationLib) loc).getLocation();
        BlockDisplay display = location.getWorld().spawn(location, BlockDisplay.class);
        return new BlockDisplayLib(display);
    }
}
