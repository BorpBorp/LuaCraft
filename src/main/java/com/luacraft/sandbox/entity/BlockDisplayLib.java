package com.luacraft.sandbox.entity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class BlockDisplayLib extends EntityLib {
    public BlockDisplayLib(BlockDisplay display) {
        super(display);

        rawset(LuaValue.valueOf("SetBlock"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue block) {
                Material material = Material.matchMaterial(block.checkjstring());
                BlockData newBlock = Bukkit.getServer().createBlockData(material);

                display.setBlock(newBlock);

                return LuaValue.NIL;
            }
        });
    }
}
