package com.luacraft.sandbox.command.args;

import com.luacraft.sandbox.command.CommandArgument;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.Commands;
import org.luaj.vm2.LuaTable;

public class StringArgument extends CommandArgument {
    private final String name;
    private final LuaTable options;
    public StringArgument(String name, LuaTable options) {
        this.name = name;
        this.options = options;
    }

    @Override
    public ArgumentBuilder buildArgument() {
//        TODO: options like type=greedy, single_word, etc
        return Commands.argument(name, StringArgumentType.string());
    }

    @Override
    public String toString() {
        return "<" + name + ": string>";
    }
}
