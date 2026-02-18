package com.luacraft.sandbox.item;

import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;

public class ItemStackLib extends LuaTable {
    public final ItemStack stack;

    public ItemStackLib(ItemStack itemstack) {
        this.stack = itemstack;

        rawset(LuaValue.valueOf("SetName"), new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                var meta = itemstack.getItemMeta();
                meta.displayName(ComponentUtils.luaValueToComponent(arg));
                itemstack.setItemMeta(meta);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetName"), new ZeroArgFunction() {
           public LuaValue call() {
                var meta = itemstack.getItemMeta();
                Component itemName = meta.displayName();
                LuaComponent holder = new LuaComponent(itemName);

                return new ComponentLib(holder.getComponent());
           } 
        });
    }

    public ItemStack getItemStack() {
        return stack;
    }
}
