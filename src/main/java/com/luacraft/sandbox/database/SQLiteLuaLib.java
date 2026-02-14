package com.luacraft.sandbox.database;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.luacraft.sandbox.component.ComponentLib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;


public class SQLiteLuaLib extends LuaTable {
    private final Plugin plugin;
    private final Map<UUID, Map<String, LuaValue>> cache = new ConcurrentHashMap<>();
    private final Map<String, String> pendingWrites = new ConcurrentHashMap<>();

    public SQLiteLuaLib(Plugin plugin) {
        this.plugin = plugin;
        rawset(LuaValue.valueOf("SetData"), new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue uuid, LuaValue key, LuaValue value) {
                String typeTag;
                Object dataToSerialize;

                if (value instanceof ComponentLib) {
                    typeTag = "Component";
                    Component realComp = ((ComponentLib) value).getComponent();
                    dataToSerialize = GsonComponentSerializer.gson().serialize(realComp);
                } else if (value.istable()) {
                    typeTag = "Table";
                    LuaTable table = value.checktable();
                    Map<String, Object> javaMap = new HashMap<>();
                    
                    for (LuaValue k : table.keys()) {
                        LuaValue v = table.get(k);

                        if (v.isnumber()) {
                            javaMap.put(k.tojstring(), v.todouble());
                        } else if (v.isboolean()) {
                            javaMap.put(k.tojstring(), v.toboolean());
                        } else {
                            javaMap.put(k.tojstring(), v.tojstring());
                        }
                    }
                    dataToSerialize = javaMap;
                } else {
                    typeTag = "Primitive";
                    dataToSerialize = value.tojstring();
                }

                Map<String, Object> wrapper = new HashMap<>();
                wrapper.put("t", typeTag);
                wrapper.put("v", dataToSerialize);

                String json = new Gson().toJson(wrapper);

                String idStr = uuid.checkstring().tojstring().toLowerCase().trim();
                UUID id = UUID.fromString(idStr);
                String k = key.checkstring().tojstring();

                cache.computeIfAbsent(id, x -> new ConcurrentHashMap<>()).put(k, value);
                pendingWrites.put(idStr + ":" + k, json);

                return LuaValue.NIL;
            }
        });

        rawset(LuaValue.valueOf("GetData"), new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue uuid, LuaValue key) {
                String idStr = uuid.checkstring().tojstring().toLowerCase().trim();
                UUID id = UUID.fromString(idStr);
                String k = key.checkstring().tojstring();

                if (cache.containsKey(id) && cache.get(id).containsKey(k)) {
                    return cache.get(id).get(k);
                }

                try (Connection conn = SQLiteLib.getInstance().getDataSource().getConnection()) {
                    String sql = "SELECT value FROM lua_data WHERE uuid = ? AND key = ?";

                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, idStr);
                        pstmt.setString(2, k);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            String json = rs.getString("value");

                            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
                            Map<String, Object> wrapper = new Gson().fromJson(json, mapType);
                            String type = (String) wrapper.get("t");
                            Object payload = wrapper.get("v");

                            LuaValue result = LuaValue.NIL;

                            if (type.equals("TextComponentImpl") || type.contains("Component")) {
                                String componentJson = (payload instanceof Map) ? new Gson().toJson(payload) : payload.toString();
                                Component comp = GsonComponentSerializer.gson().deserialize(componentJson);
                                result = new ComponentLib(comp);
                            } else if (type.equals("Table")) {
                                result = LuaValue.tableOf();
    
                                if (payload instanceof Map) {
                                    Map<?, ?> map = (Map<?, ?>) payload; 
                                    
                                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                                        String entryKey = entry.getKey().toString();
                                        Object val = entry.getValue();

                                        // Rebuild with type safety
                                        if (val instanceof Number) {
                                            result.set(entryKey, LuaValue.valueOf(((Number) val).doubleValue()));
                                        } else if (val instanceof Boolean) {
                                            result.set(entryKey, LuaValue.valueOf((Boolean) val));
                                        } else {
                                            result.set(entryKey, LuaValue.valueOf(val != null ? val.toString() : ""));
                                        }
                                    }
                                }
                            } else {
                                result = LuaValue.valueOf(payload.toString());
                            }

                            cache.computeIfAbsent(id, x -> new ConcurrentHashMap<>()).put(k, result);
                            return result;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return LuaValue.NIL;
            }
        });
    }

    public void flush() {
        if (pendingWrites.isEmpty()) return;

        Map<String, String> workload = new HashMap<>(pendingWrites);
        pendingWrites.clear();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO lua_data (uuid, key, value) VALUES (?, ?, ?) " +
                         "ON CONFLICT(uuid, key) DO UPDATE SET value = excluded.value";

            try (Connection conn = SQLiteLib.getInstance().getDataSource().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map.Entry<String, String> entry : workload.entrySet()) {
                    String[] parts = entry.getKey().split(":", 2);
                    if (parts.length < 2 ) continue;

                    pstmt.setString(1, parts[0]);
                    pstmt.setString(2, parts[1]);
                    pstmt.setString(3, entry.getValue());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                plugin.getLogger().info("Flushed to the DB");

            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to flush Lua data to SQLite!");
                e.printStackTrace();
            }
        });
    }
}