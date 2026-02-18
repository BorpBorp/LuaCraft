package com.luacraft.sandbox.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.util.ComponentUtils;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

public class ObjectiveLib extends LuaTable {
    private final Objective objective;
    public ObjectiveLib(Objective objective) {
        this.objective = objective;

        rawset(LuaValue.valueOf("SetDisplaySlot"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue displaySlot) {
                String slotName = displaySlot.checkjstring().toUpperCase();
                DisplaySlot slot = DisplaySlot.valueOf(slotName);

                objective.setDisplaySlot(slot);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetNumberFormat"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue numberFormat) {
                String formatName = numberFormat.checkjstring();

                NumberFormat numFormat;
                switch (formatName) {
                    case "blank":
                        numFormat = NumberFormat.blank();
                        break;
                    default:
                        numFormat = NumberFormat.styled(Style.empty());
                }

                objective.numberFormat(numFormat);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("SetDisplayName"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue displayName) {
                Component newDisplayName = ComponentUtils.luaValueToComponent(displayName);

                objective.displayName(newDisplayName);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetDisplayName"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaComponent holder = new LuaComponent(objective.displayName());

                return new ComponentLib(holder.getComponent());
            }
        });

        rawset(LuaValue.valueOf("GetScore"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue entry) {
                String scoreEntry = entry.checkjstring();
                Score score = objective.getScore(scoreEntry);

                return new ScoreLib(score);
            }
        });
    }

    public Objective getObjective() {
        return objective;
    }
}
