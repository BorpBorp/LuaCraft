package com.luacraft.sandbox.util;

import org.luaj.vm2.LuaValue;

import com.luacraft.sandbox.component.ComponentLib;

import net.kyori.adventure.text.Component;

public class ComponentUtils {
    public static Component luaValueToComponent(LuaValue arg) {

        

        if (arg instanceof ComponentLib lib) {
            return lib.getComponent();
        }
        
        if (arg.isuserdata()) {
            Object raw = arg.touserdata();
            if (raw instanceof Component c) return c;
        }

        return Component.text(arg.tojstring());
    }
}