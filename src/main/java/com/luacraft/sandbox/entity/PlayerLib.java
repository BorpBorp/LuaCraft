package com.luacraft.sandbox.entity;

import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.inventory.PlayerInventoryLib;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PlayerLib extends EntityLib {
    private final Player player;

    public PlayerLib(Player player) {
        super(player);
        this.player = player;

        rawset(LuaValue.valueOf("IsFlying"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.isFlying());
            }
        });

        rawset(LuaValue.valueOf("SetFlying"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                boolean fly = arg.toboolean();

                if (!fly) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                } else {
                    player.setAllowFlight(true);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetDisplayName"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaComponent holder = new LuaComponent(player.displayName());

                return new ComponentLib(holder.getComponent());
            }
        });

        rawset(LuaValue.valueOf("SetDisplayName"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue newDisplayName) {
                Component displayName;
                
                if (!newDisplayName.isnil()) {
                    displayName = ComponentUtils.luaValueToComponent(newDisplayName);
                    player.displayName(displayName);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetPrefix"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                String rawPrefix = LuaCraft.chat.getPlayerPrefix(player);
                Component prefixComp = LegacyComponentSerializer.builder()
                                        .character('&')
                                        .hexColors()
                                        .useUnusualXRepeatedCharacterHexFormat()
                                        .build()
                                        .deserialize(rawPrefix);
                
                return new ComponentLib(prefixComp);
            }
        });

        rawset(LuaValue.valueOf("SetPrefix"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue prefix) {
                String newPrefix;

                if (!prefix.isnil()) {
                    newPrefix = prefix.tojstring();
                    LuaCraft.chat.setPlayerPrefix(player, newPrefix);
                } else {
                    LuaCraft.chat.setPlayerPrefix(player, null);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetSuffix"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                String rawSuffix = LuaCraft.chat.getPlayerSuffix(player);
                Component suffixComp = LegacyComponentSerializer.builder()
                                        .character('&')
                                        .hexColors()
                                        .useUnusualXRepeatedCharacterHexFormat()
                                        .build()
                                        .deserialize(rawSuffix);
                
                return new ComponentLib(suffixComp);
            }
        });

        rawset(LuaValue.valueOf("SetSuffix"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue suffix) {
                String newSuffix;

                if (!suffix.isnil()) {
                    newSuffix = suffix.tojstring();
                    LuaCraft.chat.setPlayerSuffix(player, newSuffix);
                } else {
                    LuaCraft.chat.setPlayerSuffix(player, null);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetUUID"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getUniqueId().toString());
            }
        });

        rawset(LuaValue.valueOf("Inventory"), new PlayerInventoryLib(player));
    }

    public Player getPlayer() {
        return player;
    }
}