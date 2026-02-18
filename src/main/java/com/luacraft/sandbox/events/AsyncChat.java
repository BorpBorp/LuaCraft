package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.ComponentUtils;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public class AsyncChat implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public AsyncChat(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        for (Globals globals : allGlobals.values()) {
            LuaValue serverEvent = globals.get("ServerEvent");
            LuaValue function = serverEvent.get("OnAsyncChat");

            LuaFunction setChatFormat = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue newFormat) {
                    Component component;

                    if (!newFormat.isnil()) {
                        component = ComponentUtils.luaValueToComponent(newFormat);

                        event.renderer((source, sourceDisplayName, message, viewer) -> component);
                    }

                    return LuaValue.NIL;
                }
            };

            LuaFunction shouldChat = new LuaFunction() {
                @Override
                public LuaValue call(LuaValue shouldChat) {
                    if (shouldChat.isboolean() || shouldChat.isnil()) {
                        event.setCancelled(!shouldChat.toboolean());
                    }

                    return LuaValue.NIL;
                }
            };

            LuaFunction getMessage = new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    LuaComponent holder = new LuaComponent(player.displayName());
                    
                    return new ComponentLib(holder.getComponent());
                }
            };

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("SetChatFormat", setChatFormat);
            luaEvent.set("GetMessage", getMessage);
            luaEvent.set("ShouldChat", shouldChat);

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
