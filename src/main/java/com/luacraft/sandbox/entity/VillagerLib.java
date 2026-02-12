package com.luacraft.sandbox.entity;

import org.bukkit.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

public class VillagerLib extends EntityLib  {
    public VillagerLib(Villager villager) {
        super(villager);

        rawset(LuaValue.valueOf("SetVillagerType"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue type) {
                if (!type.isstring())
                    throw new LuaError("SetVillagerType requires a String argument");
                
                String name = type.tojstring();
                NamespacedKey key = NamespacedKey.minecraft(name.toLowerCase());
                Registry<Villager.Type> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.VILLAGER_TYPE);
                Villager.Type newType = registry.get(key);

                villager.setVillagerType(newType);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetVillagerType"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Villager.Type type = villager.getVillagerType();
                String typeString = type.getKey().getKey();

                return LuaValue.valueOf(typeString);
            }
        });
    }
}
