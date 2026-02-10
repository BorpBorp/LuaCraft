package com.luacraft.sandbox.util;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public class ColorUtils extends LuaTable {
    public static LuaFunction Color() {
        return new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue r, LuaValue g, LuaValue b) {
                LuaTable colorTable = new LuaTable();

                colorTable.set("r", r.checkint());
                colorTable.set("g", g.checkint());
                colorTable.set("b", b.checkint());

                return colorTable;
            }
        };
    }
}
