package com.luacraft.sandbox.velocity;

import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class VectorLib extends LuaTable {
    private final Vector vector;
    public VectorLib(Vector vector) {
        this.vector = vector;

        rawset(LuaValue.valueOf("GetX"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getX());
            }
        });

        rawset(LuaValue.valueOf("GetY"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getY());
            }
        });

        rawset(LuaValue.valueOf("GetZ"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getZ());
            }
        });
    }

    public Vector getVector() {
        return vector;
    }
}
