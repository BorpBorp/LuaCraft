package com.luacraft.sandbox.component;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentFactory extends VarArgFunction {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    @Override
    public Varargs invoke(Varargs args) {
        Component message = Component.empty();

        for (int i = 1; i <= args.narg(); i++) {
            LuaValue arg = args.arg(i);

            if (arg.isstring()) {
                Component parsed = SERIALIZER.deserialize(arg.tojstring());
                message = message.append(parsed);
            } else {
                Component piece = ComponentUtils.luaValueToComponent(arg);
                message = message.append(piece);
            }
        }

        return new ComponentLib(message);
    }
}
