package com.luacraft.test;

public class TestState {
    public final boolean ENABLED = System.getProperty("luacraft.testing.enabled", "false").equalsIgnoreCase("true");
}
