package com.luacraft.sandbox.entity;

import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.location.LocationLib;

public class TextDisplayFactory extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue loc) {
        Location location = ((LocationLib) loc).getLocation();
        TextDisplay display = location.getWorld().spawn(location, TextDisplay.class);
        return new TextDisplayLib(display);
    }
}