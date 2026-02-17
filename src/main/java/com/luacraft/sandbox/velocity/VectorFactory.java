package com.luacraft.sandbox.velocity;

import org.bukkit.util.Vector;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public class VectorFactory extends ThreeArgFunction {
    @Override
    public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
        Vector vector = new Vector(x.checkdouble(), y.checkdouble(), z.checkdouble());

        return new VectorLib(vector);
    }
}
