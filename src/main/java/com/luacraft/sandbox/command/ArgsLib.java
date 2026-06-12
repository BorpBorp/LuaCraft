package com.luacraft.sandbox.command;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.command.args.LiteralArgument;
import com.luacraft.sandbox.command.args.StringArgument;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.function.BiFunction;

public class ArgsLib extends LuaTable {
    public ArgsLib() {
        rawset("Literal", wrapConstructor(LiteralArgument::new, "Literal"));
        rawset("String", wrapConstructor(StringArgument::new, "String"));
    }

    public TwoArgFunction wrapConstructor(BiFunction<String,LuaTable,CommandArgument> argument, String methodName) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue name, LuaValue _options) {
                String argName = LuaErrorAssert.checkString(name, methodName, 1, null);
                LuaValue nonNullOptions = _options.isnil() ? new LuaTable() : _options;
                LuaTable options = LuaErrorAssert.checkTable(nonNullOptions, methodName, 2, null);

                return argument.apply(argName, options);
            }
        };
    }
}
