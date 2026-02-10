package com.luacraft.sandbox.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaCraft;

public class CommandLib extends LuaTable {
    public final static Map<String, Map<String, Command>> bukkitCommands = new HashMap<>();
    private static CommandMap commandMap;
    private final String fileName;

    public static void refreshAllPlayerCommands() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.updateCommands();
        }
    }
    
    public CommandLib(String file) {
        this.fileName = file;

        Server server = Bukkit.getServer();

        commandMap = server.getCommandMap();

        rawset(LuaValue.valueOf("Register"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue configTable) {
                LuaValue name = configTable.get("name");
                LuaValue description = configTable.get("description");
                LuaValue usage = configTable.get("usage");
                LuaValue permission = configTable.get("permission");
                LuaFunction onExecute = (LuaFunction) configTable.get("onExecute");

                if (!name.isstring() || name.isnil()) throw new LuaError("Name must be a string");
                if (!onExecute.isfunction() || onExecute.isnil()) throw new LuaError("onExecute must be a function");
                
                String commandName = name.tojstring();
                String commandDesc = description.isnil() ? "" : description.tojstring();
                String commandUsage = usage.isnil() ? "" : usage.tojstring();
                String commandPerm = permission.isnil() ? "" : permission.tojstring();

                Bukkit.getScheduler().runTask(LuaCraft.getPlugin(), () -> {
                    Command cmd = new CommandExecute(commandName, onExecute);
                    cmd.setDescription(commandDesc);
                    cmd.setUsage(commandUsage);
                    cmd.setPermission(commandPerm);

                    commandMap.register("LuaCraft", cmd);

                    Bukkit.getScheduler().runTask(LuaCraft.getPlugin(), () -> refreshAllPlayerCommands());

                    Map<String, Command> commandsForFile = bukkitCommands.computeIfAbsent(fileName, k -> new HashMap<>());
                    commandsForFile.put(commandName, cmd);
                });

                return LuaValue.NIL;
            }
        });
    }

    
    public static void commandUnRegister(String fileName) {
        Map<String, Command> commandsForFile = bukkitCommands.get(fileName);
        if (commandsForFile == null) return;
        
        Map<String, Command> knownCommands = commandMap.getKnownCommands();

        for (Map.Entry<String, Command> entry : commandsForFile.entrySet()) {
            String cmdName = entry.getKey();
            Command cmd = entry.getValue();
            
            cmd.unregister(commandMap);
            knownCommands.remove(cmdName);
            knownCommands.remove("LuaCraft:" + cmdName);
        }

        commandsForFile.clear();
        bukkitCommands.remove(fileName);
    }
}
