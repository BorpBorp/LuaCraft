package com.luacraft;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.luacraft.sandbox.chat.ChatLib;
import com.luacraft.sandbox.command.CommandLib;
import com.luacraft.sandbox.component.ComponentFactory;
import com.luacraft.sandbox.component.MiniMessageFactory;
import com.luacraft.sandbox.database.SQLiteLuaLib;
import com.luacraft.sandbox.events.EventTable;
import com.luacraft.sandbox.inventory.InventoryFactory;
import com.luacraft.sandbox.item.ItemStackFactory;
import com.luacraft.sandbox.location.LocationFactory;
import com.luacraft.sandbox.scoreboard.ScoreboardFactory;
import com.luacraft.sandbox.util.ColorUtils;
import com.luacraft.sandbox.util.PlayerUtil;
import com.luacraft.sandbox.util.WaitUtil;
import com.luacraft.sandbox.velocity.VectorFactory;

public class ScriptLoader {
    public record FileData(String fileName, String fileContents) {}


    private static File scriptsFolder;
    private static File addonsFolder;
    private static Plugin mainPlugin;
    private static SQLiteLuaLib dataLib;
    public static void setScriptsFolder(File pluginScriptFolder, Plugin plugin) {
        scriptsFolder = pluginScriptFolder;
        mainPlugin = plugin;
    }

    public static void setAddonsFolder(File pluginAddonFolder) {
        addonsFolder = pluginAddonFolder;
    }

    public static void passDataLib(SQLiteLuaLib lib) {
        dataLib = lib;
    }

    /**
     * Strip all the dangerous libraries out, and expose only the ones we wish to provide
     * 
     * @param globals
    */
    private static void setupGlobals(Globals globals, String fileName) {
        globals.set("os", LuaValue.NIL);
        globals.set("io", LuaValue.NIL);
        globals.set("debug", LuaValue.NIL);
        
        globals.set("Chat", new ChatLib());
        globals.set("ServerEvent", new EventTable());
        globals.set("Itemstack", new ItemStackFactory());
        globals.set("Location", new LocationFactory());
        globals.set("Component", new ComponentFactory());
        globals.set("MiniMessage", new MiniMessageFactory());
        globals.set("Inventory", new InventoryFactory());
        globals.set("Wait", WaitUtil.Wait(mainPlugin));
        globals.set("PlayerUtil", new PlayerUtil());
        globals.set("Command", new CommandLib(fileName));
        globals.set("SQL", dataLib);
        globals.set("Color", ColorUtils.Color());
        globals.set("Vector", new VectorFactory());
        globals.set("Scoreboard", new ScoreboardFactory());

        LuaValue pkg = globals.get("package");

        String scriptsPatch = scriptsFolder.getAbsolutePath() + "/?.lua";
        String addonsPath = addonsFolder.getAbsolutePath() + "/?/init.lua";

        pkg.set("path", LuaValue.valueOf(scriptsPatch + ";" + addonsPath));
        pkg.set("cpath", LuaValue.valueOf(""));
    }

    /**
     * A private helper function to ScriptLoader
     * 
     * @param file
     */
    private static FileData readScriptFile(File file) throws IOException {
        String fileContents = null;
        Path filePath;
        String fileName;

        filePath = file.toPath();
        fileName = file.getName();

        fileContents = Files.readString(filePath);

        return new FileData(fileName, fileContents);
    }

    /**
     * Load all the available scripts inside LuaCraft/scripts that end with .lua and do not start with -
     * 
     * @param allGlobals
     * @throws IOException 
     */
    public static void loadAllScripts(Map<String, Globals> allGlobals) throws IOException, LuaError { 
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String name) {
                return !name.startsWith("-") && name.endsWith(".lua");
            }
        };

        File[] allFiles = scriptsFolder.listFiles(filter);
        if (allFiles == null) {
            Bukkit.getLogger().severe("Failed to register all files in scripts, please contact author");
            return;
        }

        for (File file : allFiles) {

            Globals globals = JsePlatform.standardGlobals();

            setupGlobals(globals, file.getName());

            FileData data = readScriptFile(file);
            if (data.fileContents() == null) {
                Bukkit.getLogger().severe("Failed to read file contents, please contact author");
                continue;
            }

            CommandLib.commandUnRegister(data.fileName());

            LuaValue loadedScript = globals.load(data.fileContents(), data.fileName());

            if (loadedScript != null) {
                loadedScript.call();
                allGlobals.put(data.fileName(), globals);
            }

            Bukkit.getScheduler().runTask(mainPlugin, () -> CommandLib.refreshAllPlayerCommands());
        }
    }

    public static void loadSingleScript(Map<String, Globals> allGlobals, String fileName) throws IOException, LuaError {
        File file = new File(scriptsFolder, fileName);
        Path filePath = file.toPath();

        String fileContents = Files.readString(filePath);
        Globals globals = JsePlatform.standardGlobals();

        setupGlobals(globals, fileName);

        CommandLib.commandUnRegister(fileName);

        LuaValue loadedScript = globals.load(fileContents, fileName);

        if (loadedScript != null) {
            loadedScript.call();
            allGlobals.put(fileName, globals);
        }

        Bukkit.getScheduler().runTask(mainPlugin, () -> CommandLib.refreshAllPlayerCommands());
    }
}