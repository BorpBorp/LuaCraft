package com.luacraft.commands;

import java.io.IOException;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;

import com.luacraft.ScriptLoader;

public class LuaCommandHandler implements CommandExecutor {
    private Map<String, Globals> allGlobals;

    public LuaCommandHandler(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /lua reload[all] [<filename>]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        long startTime = System.nanoTime();

         switch (subcommand) {
            case "reload":
                if (args.length < 2) {
                    sender.sendMessage("Usage: /lua reload <filename>");
                    return true;
                }
                String fileName = args[1];
                try {
                    ScriptLoader.loadSingleScript(allGlobals, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LuaError e) {
                    sender.sendMessage("LuaError: " + e.getMessage());
                }
            break;
            case "reloadall":
                try {
                    ScriptLoader.loadAllScripts(allGlobals);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LuaError e) {
                    sender.sendMessage("LuaError: " + e.getMessage());
                }
                break;
            default:
                sender.sendMessage("Unknown command");
        }

        long endTime = System.nanoTime();

        long durationNs = endTime - startTime;
        long durationMs = durationNs / 1000000;

        sender.sendMessage("Reloaded script(s) in " + String.valueOf(durationMs) + "ms");

        return true;
    }
}
