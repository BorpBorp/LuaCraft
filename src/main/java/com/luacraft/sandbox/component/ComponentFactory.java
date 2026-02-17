package com.luacraft.sandbox.component;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.luacraft.sandbox.color.ColorLib;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
        String pendingHex = null;

        for (int i = 1; i <= args.narg(); i++) {
            LuaValue arg = args.arg(i);
            if (arg.isuserdata() && arg.touserdata() instanceof ColorLib) {
                ColorLib colorLib = (ColorLib) arg.touserdata();
                TextColor color = colorLib.getColor();

                pendingHex = String.format("&#%02X%02X%02X", color.red(), color.green(), color.blue());
            } else if (arg.isstring()) {
                String rawText = arg.tojstring();

                if (pendingHex != null) {
                    rawText = pendingHex + rawText;
                    pendingHex = null;
                }

                Component parsed = SERIALIZER.deserialize(rawText);
                message = message.append(parsed);
            } else {
                Component piece = ComponentUtils.luaValueToComponent(arg);
                message = message.append(piece);
            }
        }

        return new ComponentLib(message);
    }
}
