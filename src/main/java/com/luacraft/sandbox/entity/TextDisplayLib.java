package com.luacraft.sandbox.entity;

import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.joml.Math;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.color.ColorLib;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;

public class TextDisplayLib extends EntityLib {
    public TextDisplayLib(TextDisplay display) {
        super(display);
        rawset(LuaValue.valueOf("SetText"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue text) {
                Component component = ComponentUtils.luaValueToComponent(text);

                display.text(component);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetShadowed"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue shadowed) {
                Boolean bool = shadowed.checkboolean();

                display.setShadowed(bool);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetSeeThrough"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue seethrough) {
                Boolean bool = seethrough.checkboolean();

                display.setSeeThrough(bool);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetTextOpacity"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue opacity) {
                byte newOpacity = (byte) Math.clamp(0, 255, opacity.checkint());

                display.setTextOpacity(newOpacity);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetLineWidth"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue width) {
                int lineWidth = width.checkint();

                display.setLineWidth(lineWidth);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetAlignment"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue align) {
                String alignment = align.checkjstring();

                display.setAlignment(TextAlignment.valueOf(alignment));

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetBackgroundColor"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue color) {
                ColorLib colorLib = (ColorLib) color.touserdata();
                Color newColor = colorLib.getBukkitColor();

                display.setBackgroundColor(newColor);

                return LuaValue.NIL;
            }
        });
    }
}
