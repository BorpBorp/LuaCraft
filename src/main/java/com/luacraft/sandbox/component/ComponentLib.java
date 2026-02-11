package com.luacraft.sandbox.component;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentLib extends LuaTable {
    public ComponentLib(Component component) {
        rawset(LuaValue.valueOf("Compare"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Component otherComponent = (Component) arg.touserdata(Component.class);
                return LuaValue.valueOf(component.equals(otherComponent));
            }
        });
        
        rawset(LuaValue.valueOf("ToPlainText"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(PlainTextComponentSerializer.plainText().serialize(component));
            }
        });
    }
}
