package com.luacraft.sandbox.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.entity.PlayerLib;

public class PlayerUtil extends LuaTable {
    public PlayerUtil() {
        rawset(LuaValue.valueOf("GetPlayerFromName"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                String playerName = name.tojstring();
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) return new PlayerLib(player);
                
                return LuaValue.NIL;
            }
        });
    }
}
