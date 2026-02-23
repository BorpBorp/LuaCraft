package com.luacraft.sandbox.component;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MiniMessageFactory extends OneArgFunction {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public LuaValue call(LuaValue arg) {
        String input = arg.checkjstring();
        Component parsed = MINI_MESSAGE.deserialize(input);
        return new ComponentLib(parsed);
    }
}
