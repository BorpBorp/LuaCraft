package com.luacraft.sandbox.command.args;

import com.luacraft.sandbox.command.CommandArgument;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.Commands;
import org.luaj.vm2.LuaTable;

public class LiteralArgument extends CommandArgument {
    private final String name;
    private final LuaTable options;
    public LiteralArgument(String name, LuaTable options) {
        this.name = name;
        this.options = options;
    }

    @Override
    public ArgumentBuilder buildArgument() {
        return Commands.literal(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
