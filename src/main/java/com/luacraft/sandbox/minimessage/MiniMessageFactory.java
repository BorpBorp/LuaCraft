package com.luacraft.sandbox.minimessage;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class MiniMessageFactory extends VarArgFunction {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
        .tags(TagResolver.resolver(
            StandardTags.defaults()
        ))
        .strict(false)
        .build();

    @Override
    public Varargs invoke(Varargs args) {
        if (args.narg() == 0) {
            return LuaValue.NIL;
        }

        Component result = Component.empty();
        StringBuilder pendingText = new StringBuilder();

        for (int i = 1; i <= args.narg(); i++) {
            LuaValue arg = args.arg(i);

            if (arg instanceof ComponentLib) {
                if (pendingText.length() > 0) {
                    result = result.append(MINI_MESSAGE.deserialize(pendingText.toString()));
                    pendingText.setLength(0);
                }
                
                result = result.append(ComponentUtils.luaValueToComponent(arg));
            } else {
                pendingText.append(arg.tojstring());
            }
        }

        if (pendingText.length() > 0) {
            result = result.append(MINI_MESSAGE.deserialize(pendingText.toString()));
        }

        return new ComponentLib(result);
    }

    public static final ComponentLib colorize(Component component) {
        String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        Component parsed = MINI_MESSAGE.deserialize(plainText);
        return new ComponentLib(parsed);
    }
}
