package com.luacraft.sandbox.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class LuaComponent {
    public final Component value;
    public LuaComponent(Component value) { this.value = value; }

    @Override
    public String toString() {
        return PlainTextComponentSerializer.plainText().serialize(value);
    }

    public Component getComponent() {
        return value;
    }
}
