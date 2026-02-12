package com.luacraft.sandbox.entity;

import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.inventory.PlayerInventoryLib;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;

public class PlayerLib extends EntityLib {
    public PlayerLib(Player player) {
        super(player);

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
                return new ComponentLib(player.displayName());
            }
        });

        rawset(LuaValue.valueOf("SetDisplayName"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue newDisplayName) {
                Component displayName;
                
                if (!newDisplayName.isnil()) {
                    if (newDisplayName.isuserdata(Component.class)) {
                        displayName = ComponentUtils.luaValueToComponent(newDisplayName);
                        player.displayName(displayName);
                    } else {
                        player.displayName(Component.text(newDisplayName.tojstring()));
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("Inventory"), new PlayerInventoryLib(player));
    }
}