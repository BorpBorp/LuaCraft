package com.luacraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.luacraft.sandbox.util.ComponentUtils;
import com.luacraft.test.TestGlobals;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;

import com.luacraft.addons.java.LuaCraftAPI;
import com.luacraft.addons.java.LuaCraftGlobals;
import com.luacraft.sandbox.chat.ChatLib;
import com.luacraft.sandbox.command.CommandExecute;
import com.luacraft.sandbox.command.CommandLib;
import com.luacraft.sandbox.component.ComponentFactory;
import com.luacraft.sandbox.database.SQLiteLuaLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.enums.Enumerations;
import com.luacraft.sandbox.events.EventTable;
import com.luacraft.sandbox.inventory.InventoryFactory;
import com.luacraft.sandbox.item.ItemStackLib;
import com.luacraft.sandbox.location.BoundsLib;
import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.minimessage.MiniMessageFactory;
import com.luacraft.sandbox.particle.ParticleFactory;
import com.luacraft.sandbox.scoreboard.ScoreboardFactory;
import com.luacraft.sandbox.scriptevent.Async;
import com.luacraft.sandbox.scriptevent.OnLoad;
import com.luacraft.sandbox.scriptevent.Sync;
import com.luacraft.sandbox.util.BlockUtils;
import com.luacraft.sandbox.util.ColorUtils;
import com.luacraft.sandbox.util.WaitUtil;
import com.luacraft.sandbox.vector.VectorLib;

public class ScriptLoader {
    public record FileData(String fileName, String fileParent, String fileContents) {}

    private static final Set<String> deadScripts = new HashSet<>();

    private static final Map<String, Integer> generations = new HashMap<>();

    public static int getGeneration(String fileName) {
        return generations.getOrDefault(fileName, 0);
    }

    public static void incrementGeneration(String fileName) {
        generations.merge(fileName, 1, (a, b) -> a + b);
    }

    private static File scriptsFolder;
    private static Plugin mainPlugin;
    private static SQLiteLuaLib dataLib;
    public static void setScriptsFolder(File pluginScriptFolder, Plugin plugin) {
        scriptsFolder = pluginScriptFolder;
        mainPlugin = plugin;
    }

    public static void passDataLib(SQLiteLuaLib lib) {
        dataLib = lib;
    }

    public static void setupGlobals(Globals globals, String fileName) {
        LuaCraftGlobals lcGlobals = new LuaCraftGlobals(globals);
        for (LuaCraftAPI.AddonEntry entry : LuaCraftAPI.getRegisteredAddons()) {
            entry.addon().onLoad(lcGlobals);
        }

        //Restrictions
        globals.set("os", LuaValue.NIL);
        globals.set("io", LuaValue.NIL);
        globals.set("debug", LuaValue.NIL);
        globals.set("_G", LuaValue.NIL);
        globals.set("luajava", LuaValue.NIL);

        //Enums
        globals.set("CommandTypes", Enumerations.CommandTypeEnums());
        globals.set("Blocks", Enumerations.BlockEnums());
        globals.set("Items", Enumerations.ItemEnums());

        //Misc
        globals.set("Broadcast", ChatLib.Broadcast());
        globals.set("Color", ColorUtils.Color());
        globals.set("ServerEvent", new EventTable());
        globals.set("Sync", new Sync());
        globals.set("Async", new Async());
        globals.set("RunConsoleCommand", CommandExecute.executeConsoleCommand());

        //Player Related
        globals.set("GetPlayerFromName", PlayerLib.playerFromName());
        globals.set("GetPlayerFromUUID", PlayerLib.playerFromUUID());
        globals.set("GetAllPlayers", PlayerLib.getAllPlayers());
        globals.set("SendMessagesTo", PlayerLib.sendMessagesTo());

        //Libraries
        globals.set("Item", ItemStackLib.createTable());
        globals.set("Location", LocationLib.locationFactory());
        globals.set("Component", new ComponentFactory());
        globals.set("MiniMessage", new MiniMessageFactory());
        globals.set("Inventory", new InventoryFactory());
        globals.set("Wait", WaitUtil.Wait(mainPlugin, fileName));
        globals.set("Halt", WaitUtil.Halt(mainPlugin, globals, fileName));
        globals.set("BlockUtil", new BlockUtils());
        globals.set("Command", new CommandLib(fileName));
        globals.set("SQL", dataLib);
        globals.set("Vector", VectorLib.createTable());
        globals.set("Scoreboard", new ScoreboardFactory());
        globals.set("Particle", new ParticleFactory());
        globals.set("BoundingBox", BoundsLib.boundsFactory());

        LuaValue pkg = globals.get("package");

        String scriptsPatch = scriptsFolder.getAbsolutePath() + "/?.lua";

        pkg.set("path", LuaValue.valueOf(scriptsPatch));
        pkg.set("cpath", LuaValue.NIL);

        if (LuaCraft.getInstance().getTestState().ENABLED) {
            TestGlobals.loadAll(globals);
        }
    }

    private static FileData readScriptFile(File file) throws IOException {
        String fileContents = null;
        Path filePath;
        String fileName;
        String fileParent;

        filePath = file.toPath();
        fileParent = file.getParentFile().getName();
        fileName = file.getName();
 
        fileContents = Files.readString(filePath);

        return new FileData(fileName, fileParent, fileContents);
    }

    public static void loadAllScripts(Map<String, Globals> allGlobals) throws IOException, LuaError {
        OnLoad.OnScriptUnLoad(allGlobals);

        List<File> allFiles = collectScripts(scriptsFolder);
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

            markDead(data.fileName());

            String rawSource = data.fileContents();
            boolean useLuaJC = rawSource.contains("@LuaJC");
            String luaSource = preprocess(rawSource);

            if (useLuaJC) {
                LuaJC.install(globals);
            }

            LuaValue loadedScript = globals.load(luaSource, data.fileName());

            if (loadedScript != null) {
                LuaGuard.prepare(data.fileName());
                try {
                    loadedScript.call();
                } finally {
                    LuaGuard.cleanup();
                }
                allGlobals.put(data.fileName(), globals);
            }

            markAlive(data.fileName());
            clearUnloadedDead(data.fileName(), scriptsFolder);
        }

        OnLoad.OnScriptLoad(allGlobals);
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    public static void loadSingleScript(Map<String, Globals> allGlobals, String fileName) throws IOException, LuaError {

        Globals existingGlobals = allGlobals.get(fileName);
        if (existingGlobals != null) {
            OnLoad.OnScriptUnLoad(Map.of(fileName, existingGlobals));
        }

        if (fileName.startsWith("-")) return;

        markDead(fileName);

        File file = new File(scriptsFolder, fileName);
        Path filePath = file.toPath();

        String fileContents = Files.readString(filePath);
        String luaSource = preprocess(fileContents);

        Globals globals = JsePlatform.standardGlobals();
        setupGlobals(globals, fileName);

        LuaValue loadedScript = globals.load(luaSource, fileName);

        if (loadedScript != null) {
                LuaGuard.prepare(fileName);
                try {
                    loadedScript.call();
                } finally {
                    LuaGuard.cleanup();
                }
                allGlobals.put(fileName, globals);
            }

        OnLoad.OnScriptLoad(Map.of(fileName, allGlobals.get(fileName)));
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);

        markAlive(fileName);
        clearUnloadedDead(fileName, scriptsFolder);
    }

    private static String preprocess(String src) {
        StringBuilder out = new StringBuilder();

        String[] lines = src.split("\n");
        String currentFunction = null;
        StringBuilder currentBody = new StringBuilder();
        
        int depth = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("@")) continue;

            if (trimmed.startsWith("local function ") || trimmed.startsWith("function ")) {
                boolean isLocal = trimmed.startsWith("local");
                currentFunction = isLocal ? trimmed.split(" ")[2].split("\\(")[0]
                                        : trimmed.split(" ")[1].split("\\(")[0];
                currentBody = new StringBuilder();
                depth = 1;
            }
            else if (trimmed.matches("\\w+ = function.*") || trimmed.matches("\\w+\\.\\w+ = function.*")) {
                currentFunction = trimmed.split(" = ")[0].trim();
                currentBody = new StringBuilder();
                depth = 1;
            } else if (currentFunction != null) {
                if (trimmed.matches("(if|while|for|do|function).*") && !trimmed.matches(".*= function.*")) depth++;
                if (trimmed.equals("end")) depth--;

                currentBody.append(line).append('\n');

                if (depth == 0 && currentBody.toString().contains("Halt")) {
                    out.append(line).append('\n');
                    String safeName = currentFunction.replace(".", "_");
                    out.append("local _").append(safeName).append(" = ").append(currentFunction).append('\n');
                    out.append(currentFunction).append(" = function(...) coroutine.wrap(_")
                    .append(safeName).append(")(...) end\n");
                    currentFunction = null;
                    continue;
                } else if (depth == 0) {
                    currentFunction = null;
                }
            }

            out.append(line).append('\n');
        }

        return out.toString();
    }

    private static List<File> collectScripts(File folder) {
        List<File> result = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) return result;

        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(collectScripts(file));
            } else if (!file.getName().startsWith("-") && file.getName().endsWith(".lua")) {
                result.add(file);
            }
        }
        return result;
    }

    public static void markDead(String fileName) {
        deadScripts.add(fileName);
    }

    public static boolean isDead(String fileName) {
        return deadScripts.contains(fileName);
    }

    public static void markAlive(String fileName) {
        deadScripts.remove(fileName);
    }

    public static void clearUnloadedDead(String fileName, File folder) {
        if (!new File(folder, fileName).exists()) {
            deadScripts.remove(fileName);
        }
    }
}