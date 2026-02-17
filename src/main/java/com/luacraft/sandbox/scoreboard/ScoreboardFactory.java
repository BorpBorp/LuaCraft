package com.luacraft.sandbox.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class ScoreboardFactory extends ZeroArgFunction {
    @Override
    public LuaValue call() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        return new ScoreboardLib(scoreboard);
    }
}
