package com.luacraft.sandbox.entity;


import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.location.LocationLib;

public class ItemDisplayFactory extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue loc) {
        Location location = ((LocationLib) loc).getLocation();
        ItemDisplay display = location.getWorld().spawn(location, ItemDisplay.class);
        return new ItemDisplayLib(display);
    }
}
