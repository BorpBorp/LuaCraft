package com.luacraft;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.luacraft.test.TestState;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;

import ch.njol.skript.Skript;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;

import org.skriptlang.skript.addon.SkriptAddon;

import com.luacraft.addons.java.LuaCraftAPI;
import com.luacraft.sandbox.database.SQLiteLib;
import com.luacraft.sandbox.database.SQLiteLuaLib;
import com.luacraft.sandbox.skript.LuaFuncToSkript;

import net.milkbowl.vault.chat.Chat;

public class LuaCraft extends JavaPlugin {
    File pluginFolder;
    File pluginScriptsFolder;
    File pluginDataFile;
    File pluginBootStrapperFolder;
    Map<String, Globals> allGlobals = new HashMap<>();
    private static JavaPlugin plugin;
    private SQLiteLuaLib dataLib;
    public static Chat chat = null;
    private Boolean hasVaultInstalled;
    private TestState testState = new TestState();
    private static LuaCraft instance;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIPaperConfig(this).setNamespace("luacraft").silentLogs(true));
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.dataLib = new SQLiteLuaLib(plugin);
        instance = this;
        try {
            RegisterListeners.registerListeners(this, allGlobals);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommandAPI.onEnable();

        int pluginId = 29491;
        
        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, pluginId);

        pluginFolder = this.getDataFolder();
        pluginScriptsFolder = new File(pluginFolder, "scripts");
        pluginBootStrapperFolder = new File(pluginFolder, "bootstrapper");
        pluginDataFile = new File(pluginFolder, "storage.db");

        try {
            pluginFolder.mkdirs();
            pluginScriptsFolder.mkdirs();
            pluginBootStrapperFolder.mkdirs();
        } catch (SecurityException e) {}

        ScriptLoader.passDataLib(dataLib);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> dataLib.flush(), 1200L, 1200L);
        SQLiteLib.initialize(pluginDataFile.getAbsolutePath());



        ScriptLoader.setScriptsFolder(pluginScriptsFolder, this);

        Bukkit.getScheduler().runTask(plugin, task -> {
            try {
                ScriptLoader.loadAllScripts(allGlobals);
                Bukkit.getLogger().info("[LuaCraft] Successfully loaded all scripts without parsing errors");
                for (String fileName : allGlobals.keySet()) {
                    Bukkit.getLogger().info("\u001B[33m" + "    - " + fileName + "\u001B[0m");;
                }
            } catch (IOException e) {} catch (LuaError e) {
                Bukkit.getLogger().severe("Lua Parsing Errors found:");
                e.printStackTrace();
                Bukkit.getLogger().severe(LuaErrorAssert.makeItActuallyPretty(e.getMessage()));
            }
        });

        Plugin vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (vaultPlugin != null && vaultPlugin.isEnabled()) {
            setupChat();
        } else {
            hasVaultInstalled = false;
        }

        Plugin skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
        if (skriptPlugin != null && skriptPlugin.isEnabled()) {
            SkriptAddon addon = Skript.instance().registerAddon(LuaCraft.class, "LuaCraft");
            addon.loadModules(new LuaFuncToSkript(allGlobals));
        }

        new LuaCraftCommand(this, pluginScriptsFolder, allGlobals);
    }

    @Override
    public void onDisable() {
        for (LuaCraftAPI.AddonEntry entry : LuaCraftAPI.getRegisteredAddons()) {
            entry.addon().onUnload();
        }

        dataLib.flush();

        CommandAPI.onDisable();
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    public static LuaCraft getInstance() {
        return instance;
    }

    public Boolean hasVault() {
        return this.hasVaultInstalled;
    }

    public File getBootStrapFolder() {
        return this.pluginBootStrapperFolder;
    }

    private boolean setupChat() {
        this.hasVaultInstalled = true;
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) return false;
        chat = rsp.getProvider();
        return chat != null;
    }

    public TestState getTestState() {
        return testState;
    }
}