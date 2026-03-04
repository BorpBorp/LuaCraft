package com.luacraft.sandbox.entity;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.item.ItemStackLib;

public class ItemDisplayLib extends EntityLib {
    public ItemDisplayLib(ItemDisplay display) {
        super(display);

        rawset(LuaValue.valueOf("SetItem"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                ItemStack newItem = ((ItemStackLib) item).getItemStack();

                display.setItemStack(newItem);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetItemTransformation"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue transformation) {
                String transform = transformation.checkjstring();

                display.setItemDisplayTransform(ItemDisplayTransform.valueOf(transform));

                return LuaValue.NIL;
            }
        });
    }
}
