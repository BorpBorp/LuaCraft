package com.luacraft.sandbox.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;

public abstract class CommandArgument extends LuaTable {
//    public CommandArgument
//    abstract ArgumentType buildArgument(LuaTable options);

    public abstract ArgumentBuilder buildArgument();
}
