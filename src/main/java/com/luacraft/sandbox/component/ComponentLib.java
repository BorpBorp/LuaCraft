package com.luacraft.sandbox.component;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentLib extends LuaTable {
    private final Component component;
    public ComponentLib(Component component) {
        this.component = component;
        rawset(LuaValue.valueOf("Compare"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue comp2) {
                Component otherComponent = ComponentUtils.luaValueToComponent(comp2);
                return LuaValue.valueOf(otherComponent.equals(component));
            }
        });

        rawset(LuaValue.valueOf("ToString"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(PlainTextComponentSerializer.plainText().serialize(component));
            }
        });
    }

    public Component getComponent() {
        return component;
    }
}