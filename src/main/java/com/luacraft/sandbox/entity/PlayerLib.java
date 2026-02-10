package com.luacraft.sandbox.entity;

import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.inventory.PlayerInventoryLib;

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

        rawset(LuaValue.valueOf("Inventory"), new PlayerInventoryLib(player));
    }
}