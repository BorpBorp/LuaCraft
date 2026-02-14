package com.luacraft.sandbox.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatLib extends LuaTable {
    public ChatLib() {
        rawset(LuaString.valueOf("Broadcast"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Component message;

                if (!arg.isnil()) {
                    message = ComponentUtils.luaValueToComponent(arg);
                } else {
                    throw new LuaError("broadcast expects a String or Component");
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(message);
                }
                Bukkit.getLogger().info(PlainTextComponentSerializer.plainText().serialize(message));

                return LuaValue.NIL;
            }
        });

        rawset(LuaString.valueOf("clearAll"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (int i = 0; i < 500; i++) {
                        player.sendMessage(" ");
                    }
                }

                return LuaValue.NIL;
            }
        });
    }
}