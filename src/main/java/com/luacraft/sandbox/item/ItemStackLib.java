package com.luacraft.sandbox.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.util.ComponentUtils;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class ItemStackLib extends LuaTable {
    public final ItemStack stack;

    public ItemStackLib(ItemStack itemstack) {
        this.stack = itemstack;

        rawset(LuaValue.valueOf("SetName"), new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                ItemMeta meta = itemstack.getItemMeta();
                Component comp = ComponentUtils.luaValueToComponent(arg);
                Component nonItalic = comp.decoration(TextDecoration.ITALIC, false);
                meta.displayName(nonItalic);
                itemstack.setItemMeta(meta);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetName"), new ZeroArgFunction() {
           public LuaValue call() {
                ItemMeta meta = itemstack.getItemMeta();
                Component itemName = meta.displayName();
                LuaComponent holder = new LuaComponent(itemName);

                return new ComponentLib(holder.getComponent());
           } 
        });

        rawset(LuaValue.valueOf("GetType"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(itemstack.getType().toString());
            }
        });

        rawset(LuaValue.valueOf("SetLore"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (itemstack == null) {
                    return LuaValue.NIL;
                }
                List<Component> loreList = new ArrayList<>();
                if (arg.isstring()) {
                    String lore = arg.checkjstring();
                    for (String line : lore.split("\n")) {
                        loreList.add(ComponentUtils.parseLegacy(line));
                    }
                } else if (arg.istable()) {
                    int n = arg.length();
                    for (int i = 1; i <= n; i++) {
                        LuaValue elem = arg.get(i);
                        if (elem.isstring()) {
                            loreList.add(ComponentUtils.parseLegacy(elem.checkjstring()));
                        } else {
                            loreList.add(ComponentUtils.luaValueToComponent(elem));
                        }
                    }
                } else if (arg instanceof ComponentLib) {
                    loreList.add(((ComponentLib) arg).getComponent());
                }

                itemstack.lore(loreList);
                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetFlags"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {

                ItemMeta meta = itemstack.getItemMeta();

                Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.LUCK);

                if (modifiers == null || modifiers.isEmpty()) {
                    meta.addAttributeModifier(Attribute.LUCK, new AttributeModifier(
                        new NamespacedKey(LuaCraft.getPlugin(), "hidden_attr"),
                        0,
                        AttributeModifier.Operation.ADD_NUMBER
                    ));
                }

                if (arg.isstring()) {
                    String flag = arg.checkjstring();
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                    itemstack.setItemMeta(meta);
                } else if (arg.istable()) {
                    int n = arg.length();
                        for (int i = 1; i <= n; i++) {
                            LuaValue elem = arg.get(i);
                        if (elem.isstring()) {
                            meta.addItemFlags(ItemFlag.valueOf(elem.checkjstring()));
                            itemstack.setItemMeta(meta);
                        } else {
                            throw new LuaError("SetFlags (TableValue) requires only string elements");
                        }
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("HasMetaData"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                PersistentDataContainerView pdc = itemstack.getPersistentDataContainer();
                NamespacedKey nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());

                return LuaValue.valueOf(pdc.has(nameKey));
            }
        });

        rawset(LuaValue.valueOf("SetMetaData"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue value) {
                NamespacedKey nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());

                if (value.isstring()) {
                    itemstack.editPersistentDataContainer(pdc -> {
                        pdc.set(nameKey, PersistentDataType.STRING, value.checkjstring());
                    });
                } else if (value.isnumber()) {
                    itemstack.editPersistentDataContainer(pdc -> {
                        pdc.set(nameKey, PersistentDataType.DOUBLE, value.checkdouble());
                    });
                } else if (value.isboolean()) {
                    itemstack.editPersistentDataContainer(pdc -> {
                        pdc.set(nameKey, PersistentDataType.BOOLEAN, value.checkboolean());
                    });
                }

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetMetaData"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                PersistentDataContainerView pdc = itemstack.getPersistentDataContainer();
                NamespacedKey nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());

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

        rawset(LuaValue.valueOf("Add"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue amount) {
                Integer amt = amount.checkint();

                itemstack.add(amt);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("Subtract"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue amount) {
                Integer amt = amount.checkint();

                itemstack.subtract(amt);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("Unbreakable"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue unbreaking) {
                ItemMeta meta = itemstack.getItemMeta();

                meta.setUnbreakable(unbreaking.checkboolean());

                itemstack.setItemMeta(meta);

                return LuaValue.NIL;
            }
        });
    }

    public ItemStack getItemStack() {
        return stack;
    }
}
