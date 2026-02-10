package com.luacraft.sandbox.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class WaitUtil extends LuaTable {
    public static LuaFunction Wait(Plugin plugin) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg, LuaValue callback) {
                if (!callback.isfunction()) return LuaValue.NIL;

                int waitTime = arg.toint();

                if (waitTime < 0) waitTime = 1;

                LuaValue func = callback.checkfunction();

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        func.call();
                    }
                    
                };
                Bukkit.getScheduler().runTaskLater(plugin, task, waitTime);

                return LuaValue.NIL;
            }
        };
    }
}
