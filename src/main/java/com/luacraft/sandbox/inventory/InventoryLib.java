package com.luacraft.sandbox.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class InventoryLib extends LuaTable {
    public InventoryLib(Inventory inventory) {

        rawset(LuaValue.valueOf("GiveItem"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                if (inventory != null) {
                    ItemStack i = ((ItemStackLib) item).stack;
                    inventory.addItem(i);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetItem"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue slot, LuaValue item) {
                if (inventory != null) {
                    ItemStack i = ((ItemStackLib) item).stack;
                    inventory.setItem(slot.toint(), i);
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("Open"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ply) {
                Player player = null;

                if (ply instanceof PlayerLib lib) {
                    player = lib.getPlayer();
                }

                if (player != null) {
                    player.updateInventory();
                    player.openInventory(inventory);
                }
                    

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("UpdateInventory"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                for (HumanEntity viewer : inventory.getViewers()) {
                    if (viewer instanceof Player player) {
                        player.updateInventory();
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetMetaTag"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
                InventoryHolder holder = inventory.getHolder();
                if (holder instanceof LuaInventoryHolder) {
                    LuaInventoryHolder luaHolder = (LuaInventoryHolder) holder;
                    if (luaHolder != null) {
                        luaHolder.getScriptData().set("meta", value);
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetMetaTag"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                InventoryHolder holder = inventory.getHolder();
                if (holder instanceof LuaInventoryHolder) {
                    LuaInventoryHolder luaHolder = (LuaInventoryHolder) holder;
                    if (luaHolder != null) {
                        return luaHolder.getScriptData().get("meta");
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetSize"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(inventory.getSize());
            }
        });

        rawset(LuaValue.valueOf("GetSlots"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable items = new LuaTable();
                ItemStack[] contents = inventory.getContents();
                for (int i = 0; i < contents.length; i++) {
                    ItemStack item = contents[i];
                    if (item != null && item.getType() != Material.AIR) {
                        items.set(i + 1, new ItemStackLib(item));
                    }
                }

                return items;
            }
        });
    }
}