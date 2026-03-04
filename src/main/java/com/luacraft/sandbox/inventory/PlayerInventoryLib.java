package com.luacraft.sandbox.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.item.ItemStackLib;

public class PlayerInventoryLib extends InventoryLib {
    public PlayerInventoryLib(Player player) {
        super(player.getInventory());

        rawset(LuaValue.valueOf("GetMiniCraftGrid"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                InventoryView view = player.getOpenInventory();
                CraftingInventory craftingInv;
                if (view.getTopInventory() instanceof CraftingInventory) {
                    craftingInv = (CraftingInventory) view.getTopInventory();
                    return new InventoryLib(craftingInv);
                }
                return LuaValue.NIL;
            } 
        });

        rawset(LuaValue.valueOf("GetItemInMainHand"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new ItemStackLib(player.getInventory().getItemInMainHand());
            }
        });

        rawset(LuaValue.valueOf("GetItemInOffHand"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new ItemStackLib(player.getInventory().getItemInOffHand());
            }
        });
    }
}
