package com.luacraft.sandbox.block;

import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.location.LocationLib;

public class BlockLib extends LuaTable {
    public BlockLib(Block block) {
        rawset(LuaValue.valueOf("GetType"), new ZeroArgFunction() {
           @Override
           public LuaValue call() {
                return LuaValue.valueOf(block.getType().toString());
           } 
        });
        
        rawset(LuaValue.valueOf("GetName"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                String prettyName = WordUtils.capitalizeFully(block.getType().toString().replace("_", " "));

                return LuaValue.valueOf(prettyName);
            }
        });

        rawset(LuaValue.valueOf("IsType"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue type) {
                String prettyName = WordUtils.capitalizeFully(block.getType().toString().replace("_", " "));
                String blockName = type.tojstring();

                return LuaValue.valueOf(prettyName.equals(blockName));
            }
        });

        rawset(LuaValue.valueOf("SetBlock"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue newBlock) {
                Material material = Material.matchMaterial(newBlock.tojstring());
                block.setType(material);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetLocation"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(block.getLocation());
            }
        });
    }
}
