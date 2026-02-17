package com.luacraft.sandbox.entity;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Pig;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class AnimalsLib extends LivingEntityLib {
    public AnimalsLib(Animals animal) {
        super(animal);
        
        rawset(LuaValue.valueOf("SetSaddle"), new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                if (animal instanceof Pig pig) {
                    pig.setSaddle(arg.toboolean());
                }
                
                return LuaValue.NIL;
            }
        });
    }
}
