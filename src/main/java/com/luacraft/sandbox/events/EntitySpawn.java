package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.entity.EntityLib;
import com.luacraft.sandbox.location.LocationLib;

public class EntitySpawn implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public EntitySpawn(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void EntitySpawnEvent(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        for (Globals globals : allGlobals.values()) {
            LuaValue serverEvent = globals.get("ServerEvent");
            LuaValue function = serverEvent.get("OnEntitySpawn");

            LuaFunction getLocation = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new LocationLib(event.getLocation());
                }
            };

            LuaFunction shouldSpawn = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!should.toboolean());

                    return LuaValue.NIL;
                }
            };

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Entity", new EntityLib(entity));
            luaEvent.set("GetLocation", getLocation);
            luaEvent.set("ShouldSpawn", shouldSpawn);
            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(CoerceJavaToLua.coerce(luaEvent));
                } catch(LuaError e) {
                    Bukkit.getLogger().info("Lua Script Error: " + e.getMessage());
                }
            }
        }
    }
}
