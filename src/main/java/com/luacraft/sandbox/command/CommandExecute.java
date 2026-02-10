package com.luacraft.sandbox.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.entity.PlayerLib;

public class CommandExecute extends Command {
    private final LuaFunction function;

    public CommandExecute(String name, LuaFunction function) {
        super(name);
        this.function = function;
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        LuaValue senderValue;
        LuaTable argsValue = new LuaTable();

        if (sender instanceof Player player) {
            senderValue = new PlayerLib(player);
        } else {
            // TEMP: Console sender is raw Java userdata. Replace with CommandSenderLib wrapper.
            senderValue = CoerceJavaToLua.coerce(sender);
        }

        for (int i = 0; i < args.length; i++) {
            argsValue.set(i + 1, LuaValue.valueOf(args[i]));
        }

        function.call(senderValue, argsValue);

        return true;
    }
}
