package com.luacraft;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.annotation.Nullable;

public class LuaErrorAssert {
    private static Player reloader;

    public static String makeItActuallyPretty(String rawError) {
        if (rawError == null) return "Unknown error";

        String clean = rawError.replaceAll("\\[string \"(.+?)\"\\]:", "$1:");

        String[] parts = clean.split(":", 3);

        if (parts.length >= 3) {
            String file = parts[0].trim();
            String line = parts[1].trim();
            String message = parts[2].trim();
            message = message.replaceAll("\\(to close '(.*)' at line (\\d+)\\)", "to close '$1' on line $2");

            return file + " on line " + line + ": " + message;
        }

        return clean;
    }

    public static void setReloader(CommandSender sender) {
        if (sender instanceof Player player) {
            reloader = player;
        }
    }

    public static boolean checkBoolean(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.isboolean()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' Boolean expected, got " + value.typename()), player);
        }

        return value.toboolean();
    }

    public static String checkString(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.isstring()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' String expected, got " + value.typename()), player);
        }

        return value.tojstring();
    }

    public static int checkInt(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.isnumber()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' Number expected, got " + value.typename()), player);
        }

        return value.toint();
    }

    public static double checkDouble(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.isnumber()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' Number expected, got " + value.typename()), player);
        }

        return value.todouble();
    }

    public static float checkFloat(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.isnumber()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' Number expected, got " + value.typename()), player);
        }

        return value.tofloat();
    }

    public static long checkLong(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.isnumber()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' Number expected, got " + value.typename()), player);
        }

        return value.tolong();
    }

    public static LuaTable checkTable(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.istable()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' Table expected, got " + value.typename()), player);
        }

        return (LuaTable) value;
    }

    public static LuaFunction checkFunction(LuaValue value, String funcName, int argNum, Player player) {
        if (!value.isfunction()) {
            throwError(new LuaError("[LuaCraft] Bad Argument #" + argNum + " to '" + funcName + "' Function expected, got " + value.typename()), player);
        }

        return (LuaFunction) value;
    }

    public static void throwError(LuaError error, @Nullable Player player) throws LuaError {
        reloader = reloader != null ? reloader : player;
        if (reloader != null) {
            reloader.sendMessage(error.getMessage());
        }
        throw error;
    }

    public static void throwError(String message, @Nullable Player player) throws LuaError {
        throwError(new LuaError("[LuaCraft] " + message), player);
    }
}