package com.luacraft.sandbox.command;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.command.args.LiteralArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.Command;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class NewCommandLib extends LuaTable {
    public NewCommandLib(String file) {
        rawset("Register", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 2) {
                    LuaErrorAssert.throwError("Command.Register expected at least 2 arguments, got " + args.narg(), null);
                }

                LuaErrorAssert.checkString(args.arg(1), "Register", 1, null);
                LuaErrorAssert.checkFunction(args.arg(args.narg()), "Register", 1, null);

                CommandArgument[] commandArguments = new CommandArgument[args.narg() - 1];
                for (int i = 1; i < args.narg(); i++) {
                    if (args.isstring(i)) {
                        commandArguments[i-1] = new LiteralArgument(args.arg(i).tojstring(), new LuaTable());
                        continue;
                    }
                    if (args.istable(i)) {
                        LuaTable table = args.checktable(i);
                        try {
                            CommandArgument arg = (CommandArgument) table;
                            commandArguments[i-1] = arg;
                            continue;
                        } catch (ClassCastException e) {
                            LuaErrorAssert.throwError("Command argument " + i + " while registering command '" + argumentArrayToString(commandArguments) + "' was a table, but not a valid argument", null);
                        }
                    }
                    LuaErrorAssert.throwError("Command argument " + i + " while registering command '" + argumentArrayToString(commandArguments) + "' was a table, but not a valid argument", null);
                }

                LuaCraft.getInstance().getLogger().info("Registering command literals " + argumentArrayToString(commandArguments));

                return LuaValue.NIL;
            }
        });

    }

    private String argumentArrayToString(CommandArgument[] args) {
        StringBuilder sb = new StringBuilder();
        for (CommandArgument arg : args) {
            sb.append(arg.toString()).append(" ");
        }
        return sb.toString().trim();
    }
}
