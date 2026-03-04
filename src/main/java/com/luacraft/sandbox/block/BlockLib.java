package com.luacraft.sandbox.block;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.sandbox.location.LocationLib;

public class BlockLib extends LuaTable {
    private Block block;
    private static Set<String> keys = new HashSet<>();

    public BlockLib(Block block) {
        this.block = block;

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

        rawset(LuaValue.valueOf("HasMetaData"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                TileState tileState = getTileState(block);
                PersistentDataContainer pdc;
                NamespacedKey nameKey;
                if (tileState == null) {
                    pdc = block.getChunk().getPersistentDataContainer();
                    Location loc = block.getLocation();
                    String blockKey = loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getWorld().getName() + "-" + key.tojstring();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), blockKey);
                } else {
                    pdc = tileState.getPersistentDataContainer();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());
                }

                return LuaValue.valueOf(pdc.has(nameKey));
            }
        });

        rawset(LuaValue.valueOf("SetMetaData"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue value) {
                TileState tileState = getTileState(block);
                PersistentDataContainer pdc;
                NamespacedKey nameKey;
                if (tileState == null) {
                    pdc = block.getChunk().getPersistentDataContainer();
                    Location loc = block.getLocation();
                    String blockKey = loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getWorld().getName() + "-" + key.tojstring();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), blockKey);
                    keys.add(key.tojstring());
                } else {
                    pdc = tileState.getPersistentDataContainer();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());
                    keys.add(key.tojstring());
                }

                if (value.isstring()) {
                    pdc.set(nameKey, PersistentDataType.STRING, value.checkjstring());
                } else if (value.isnumber()) {
                    pdc.set(nameKey, PersistentDataType.DOUBLE, value.checkdouble());
                } else if (value.isboolean()) {
                    pdc.set(nameKey, PersistentDataType.BOOLEAN, value.checkboolean());
                }

                if (tileState != null)
                    tileState.update();
                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetMetaData"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                TileState tileState = getTileState(block);
                PersistentDataContainer pdc;
                NamespacedKey nameKey;
                if (tileState == null) {
                    pdc = block.getChunk().getPersistentDataContainer();
                    Location loc = block.getLocation();
                    String blockKey = loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getWorld().getName() + "-" + key.tojstring();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), blockKey);
                } else {
                    pdc = tileState.getPersistentDataContainer();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());
                }

                if (pdc.has(nameKey, PersistentDataType.STRING)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.STRING));
                } else if (pdc.has(nameKey, PersistentDataType.DOUBLE)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.DOUBLE));
                } else if (pdc.has(nameKey, PersistentDataType.BOOLEAN)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.BOOLEAN));
                }

                return LuaValue.NIL;
            }
        });
    }

    private TileState getTileState(Block block) {
        if (block.getState() instanceof TileState tileState) {
            return tileState;
        }

        return null;
    }

    public Block getBlock() {
        return block;
    }

    public static Set<String> getKeys() {
        return keys;
    }
}
