package com.luacraft;

import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.luaj.vm2.Globals;

import com.luacraft.sandbox.events.AsyncChat;
import com.luacraft.sandbox.events.InventoryClick;
import com.luacraft.sandbox.events.PlayerBlockBreak;
import com.luacraft.sandbox.events.PlayerInteract;
import com.luacraft.sandbox.events.PlayerJoin;
import com.luacraft.sandbox.events.PlayerMove;
import com.luacraft.sandbox.events.PlayerQuit;

public class RegisterListeners {
    public static void registerListeners(Plugin plugin, Map<String, Globals> allGlobals) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoin(allGlobals), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerQuit(allGlobals), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerBlockBreak(allGlobals), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerMove(allGlobals), plugin);
        plugin.getServer().getPluginManager().registerEvents(new AsyncChat(allGlobals), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InventoryClick(allGlobals), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerInteract(allGlobals), plugin);
    }
}
