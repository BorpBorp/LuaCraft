package com.luacraft.sandbox.color;

import net.kyori.adventure.text.format.TextColor;

public class ColorLib {
    private final TextColor color;

    public ColorLib(int r, int g, int b) {
        int red = Math.clamp(r, 0, 255);
        int green = Math.clamp(g, 0, 255);
        int blue = Math.clamp(b, 0, 255);

        this.color = TextColor.color(red, green, blue);
    }

    public TextColor getColor() {
        return color;
    }
}
