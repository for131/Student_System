package org.example.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * 自定义 User TypeAdapter：
     * 写出时序列化具体子类，读取时根据 role 字段决定实例化哪个子类。
     */
    private static class UserTypeAdapter extends TypeAdapter<User> {
        private final Gson delegateGson = new GsonBuilder().setPrettyPrinting().create();

        @Override
        public void write(JsonWriter out, User user) throws IOException {
            if (user == null) { out.nullValue(); return; }
            JsonObject obj = delegateGson.toJsonTree(user).getAsJsonObject();
            if (!obj.has("role") && user.getRole() != null) {
                obj.addProperty("role", user.getRole());
            }
            new Gson().toJson(obj, out);
        }

        @Override
        public User read(JsonReader in) throws IOException {
            JsonObject obj = JsonParser.parseReader(in).getAsJsonObject();
            String role = obj.has("role") ? obj.get("role").getAsString() : "";
            return switch (role) {
                case "Student" -> delegateGson.fromJson(obj, Student.class);
                case "Parent"  -> delegateGson.fromJson(obj, Parent.class);
                case "Admin"   -> delegateGson.fromJson(obj, Admin.class);
                default -> {
                    System.err.println("未知角色: " + role);
                    yield delegateGson.fromJson(obj, Admin.class);
                }
            };
        }
    }

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(User.class, new UserTypeAdapter())
            .create();

    public static <T> void saveToJson(String filePath, List<T> list) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("无法创建目录: " + parentDir.getAbsolutePath());
                return;
            }
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }

    public static <T> List<T> readFromJson(String filePath, TypeToken<List<T>> typeToken) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            List<T> result = gson.fromJson(reader, typeToken.getType());
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}