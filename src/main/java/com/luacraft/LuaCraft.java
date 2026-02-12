package com.luacraft;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;

import com.luacraft.commands.LuaCommandHandler;

public class LuaCraft extends JavaPlugin {
    File pluginFolder;
    File pluginScriptsFolder;
    File pluginAddonsFolder;
    Map<String, Globals> allGlobals = new HashMap<>();
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        int pluginId = 29491;
        Metrics metrics = new Metrics(this, pluginId);

        RegisterListeners.registerListeners(this, allGlobals);

        pluginFolder = this.getDataFolder();
        pluginScriptsFolder = new File(pluginFolder, "scripts");
        pluginAddonsFolder = new File(pluginFolder,  "addons");

        if (!pluginScriptsFolder.exists()) {
            pluginScriptsFolder.mkdirs();
            Bukkit.getLogger().info("Created scripts folder located at: " + pluginScriptsFolder.getAbsolutePath());
        } else {
            Bukkit.getLogger().info("Scripts folder already exists, skipping...");
        }

        if (!pluginAddonsFolder.exists()) {
            pluginAddonsFolder.mkdirs();
            Bukkit.getLogger().info("Created addons folder location at: " + pluginAddonsFolder.getAbsolutePath());
        } else {
            Bukkit.getLogger().info("Addons folder already exists, skipping...");
        }

        ScriptLoader.setScriptsFolder(pluginScriptsFolder, this);

        try {
            ScriptLoader.loadAllScripts(allGlobals);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LuaError e) {
            e.getMessage();
        }
        
        getCommand("lua").setExecutor(new LuaCommandHandler(allGlobals));
    }

    @Override
    public void onDisable() {
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}