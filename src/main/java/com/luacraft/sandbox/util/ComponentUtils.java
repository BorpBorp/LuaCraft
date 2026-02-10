package com.luacraft.sandbox.util;

import org.luaj.vm2.LuaValue;

import net.kyori.adventure.text.Component;

public class ComponentUtils {
    public static Component luaValueToComponent(LuaValue arg) {
        if (arg.isuserdata(Component.class)) {
            return (Component) arg.touserdata(Component.class);
        }
        return Component.text(arg.tojstring());
    }
}