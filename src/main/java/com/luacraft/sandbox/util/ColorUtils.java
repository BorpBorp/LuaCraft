package com.luacraft.sandbox.util;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

import com.luacraft.sandbox.color.ColorLib;

public class ColorUtils extends LuaTable {
    public static LuaFunction Color() {
        return new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue r, LuaValue g, LuaValue b) {
                return LuaValue.userdataOf(new ColorLib(r.checkint(), g.checkint(), b.checkint()));
            }
        };
    }
}