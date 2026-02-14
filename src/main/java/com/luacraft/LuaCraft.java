package com.luacraft;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;

import com.luacraft.commands.LuaCommandHandler;
import com.luacraft.sandbox.database.SQLiteLib;
import com.luacraft.sandbox.database.SQLiteLuaLib;

import net.milkbowl.vault.chat.Chat;

public class LuaCraft extends JavaPlugin {
    File pluginFolder;
    File pluginScriptsFolder;
    File pluginAddonsFolder;
    File pluginDataFile;
    Map<String, Globals> allGlobals = new HashMap<>();
    private static JavaPlugin plugin;
    private SQLiteLuaLib dataLib;
    public static Chat chat = null;

    @Override
    public void onEnable() {
        plugin = this;
        this.dataLib = new SQLiteLuaLib(plugin);

        RegisterListeners.registerListeners(this, allGlobals);

        pluginFolder = this.getDataFolder();
        pluginScriptsFolder = new File(pluginFolder, "scripts");
        pluginAddonsFolder = new File(pluginFolder,  "addons");
        pluginDataFile = new File(pluginFolder, "storage.db");

        ScriptLoader.passDataLib(dataLib);
        
        Bukkit.getScheduler().runTaskTimer(plugin, () -> dataLib.flush(), 200L, 200L);

        SQLiteLib.initialize(pluginDataFile.getAbsolutePath());

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
        dataLib.flush();
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @SuppressWarnings("unused")
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) return false;
        chat = rsp.getProvider();
        return chat != null;
    }
}