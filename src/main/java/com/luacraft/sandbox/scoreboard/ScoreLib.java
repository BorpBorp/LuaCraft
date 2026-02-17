package com.luacraft.sandbox.scoreboard;

import org.bukkit.scoreboard.Score;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.luacraft.sandbox.util.ComponentUtils;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;

public class ScoreLib extends LuaTable {
    private final Score score;

    public ScoreLib(Score score) {
        this.score = score;

        rawset(LuaValue.valueOf("SetScore"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
                score.setScore(value.checkint());

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetNumberFormat"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue type, LuaValue component) {
                String typeName = type.checkjstring().toLowerCase();

                switch(typeName) {
                    case "fixed":
                        Component newComp = ComponentUtils.luaValueToComponent(component);
                        score.numberFormat(NumberFormat.fixed(newComp));
                        break;
                }

                return LuaValue.NIL;
            }
        });
    }

    public Score getScore() {
        return score;
    }
}
