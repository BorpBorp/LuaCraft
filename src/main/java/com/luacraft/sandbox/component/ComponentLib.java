package com.luacraft.sandbox.component;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.minimessage.MiniMessageFactory;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentLib extends LuaTable {
    private final Component component;
    public ComponentLib(Component component) {
        this.component = component;
        rawset(LuaValue.valueOf("Compare"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue comp2) {
                Component otherComponent = ComponentUtils.luaValueToComponent(comp2);
                String json1 = GsonComponentSerializer.gson().serialize(component);
                String json2 = GsonComponentSerializer.gson().serialize(otherComponent);
        
                return LuaValue.valueOf(json1.equals(json2));
            }
        });

        rawset(LuaValue.valueOf("ToString"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(PlainTextComponentSerializer.plainText().serialize(component));
            }
        });

        rawset(LuaValue.valueOf("Italics"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue bool) {
                Component modified = component.decoration(TextDecoration.ITALIC, bool.checkboolean());
                return new ComponentLib(modified);
            }
        });

        rawset(LuaValue.valueOf("ClickEvent"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue type, LuaValue value) {
                String clickEventType = type.tojstring();
                String clickEventValue = value.tojstring();
                Component newComponent;

                switch (clickEventType) {
                    case "url":
                        newComponent = component.clickEvent(ClickEvent.openUrl(clickEventValue));
                        return new ComponentLib(newComponent);
                    case "command":
                        newComponent = component.clickEvent(ClickEvent.runCommand(clickEventValue));
                        return new ComponentLib(newComponent);
                    case "suggestcommand":
                        newComponent = component.clickEvent(ClickEvent.suggestCommand(clickEventValue));
                        return new ComponentLib(newComponent);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("Colorize"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue type) {
                switch (type.checkjstring().toLowerCase()) {
                    case "minimessage":
                        return MiniMessageFactory.colorize(component);
                    case "component":
                        return ComponentFactory.colorize(component);
                    default:
                        return ComponentFactory.colorize(component);
                }
            }
        });
    }

    public Component getComponent() {
        return component;
    }
}