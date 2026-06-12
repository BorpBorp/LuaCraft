package com.luacraft.test;

import org.bukkit.Bukkit;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestGlobals {
    public static void loadAll(Globals globals) {
        globals.set("io", new TestIoGlobals());
        globals.set("os", new TestOsGlobals());
        globals.load(new DebugLib());
        globals.set("TestReflect", new TestReflectGlobals());

        try (InputStream stream = TestGlobals.class.getResourceAsStream("/lester.lua")) {
            LuaValue value = globals.load(stream, "testlib", "t", globals).call();
            globals.set("Test", value);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to load the test library: " + e.getMessage());
        }
    }

    private static class TestIoGlobals extends LuaTable {
        public TestIoGlobals() {
            rawset("write", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    for (int i = 1, n = args.narg(); i <= n; i++)
                        System.out.print(args.checkstring(i));
                    return args;
                }
            });
            rawset("flush", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    System.out.flush();
                    return args;
                }
            });
        }
    }

    private static final long t0 = System.currentTimeMillis();

    private static class TestOsGlobals extends LuaTable {
        public TestOsGlobals() {
            rawset("getenv", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue envKey) {
                    String env = System.getProperty(envKey.checkjstring());
                    return env != null ? LuaString.valueOf(env) : NIL;
                }
            });
            rawset("clock", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaDouble.valueOf((System.currentTimeMillis() - t0) / 1000.0);
                }
            });
        }
    }


    private static class TestReflectGlobals extends LuaTable {
        public TestReflectGlobals() {
            rawset("GetField", new TwoArgFunction() {
                @Override
                public LuaValue call(LuaValue luaValue, LuaValue fieldName) {
                    Object obj = luaValue;
                    if (luaValue.type() == LuaValue.TUSERDATA) {
                        obj = luaValue.touserdata();
                    }

                    try {
                        Field field = obj.getClass().getDeclaredField(fieldName.checkjstring());
                        field.setAccessible(true);
                        return CoerceJavaToLua.coerce(field.get(obj));
                    } catch (NoSuchFieldException e) {
                        System.out.println("Failed to find field: " + fieldName.checkjstring() + " in class " + obj.getClass().getName());
                        return NIL;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            rawset("CallMethod", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs varargs) {
                    if (varargs.narg() < 2) {
                        throw new LuaError("Expected at least 2 arguments: object and method name");
                    }
                    LuaValue luaValue = varargs.arg(1);
                    LuaValue methodName = varargs.arg(2);
                    Object obj = luaValue;
                    if (luaValue.type() == LuaValue.TUSERDATA) {
                        obj = luaValue.touserdata();
                    }

                    try {
                        System.out.println(Arrays.toString(obj.getClass().getDeclaredFields()));
                        Method method = obj.getClass().getDeclaredMethod(methodName.checkjstring());
                        method.setAccessible(true);
                        Object[] javaArgs = new Object[varargs.narg() - 2];
                        for (int i = 3; i <= varargs.narg(); i++) {
                            LuaValue arg = varargs.arg(i);
                            javaArgs[i - 3] = CoerceLuaToJava.coerce(arg, Object.class);
                        }
                        Object result = method.invoke(obj, javaArgs);
                        return CoerceJavaToLua.coerce(result);
                    } catch (NoSuchMethodException e) {
                        System.out.println("Failed to find method: " + methodName.checkjstring() + " in class " + obj.getClass().getName());
                        return NIL;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            rawset("Coerce", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue luaValue) {
                    return CoerceJavaToLua.coerce(luaValue.checktable());
                }
            });
        }
    }
}
