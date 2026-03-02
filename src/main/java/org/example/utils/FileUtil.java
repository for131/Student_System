package org.example.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    // 创建 Gson 对象，setPrettyPrinting 让生成的 JSON 文件带缩进，方便肉眼阅读
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 将 List 集合保存为 JSON 文件
     * @param filePath 文件路径，如 "data/users.json"
     * @param list 要保存的数据集合
     */
    public static <T> void saveToJson(String filePath, List<T> list) {
        // 使用 try-with-resources 自动关闭流，指定 UTF-8 防止中文乱码
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }

    /**
     * 从 JSON 文件读取数据并转回 List 集合
     * @param filePath 文件路径
     * @param typeToken 泛型令牌，调用方式：new TypeToken<List<Student>>(){}
     */
    public static <T> List<T> readFromJson(String filePath, TypeToken<List<T>> typeToken) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>(); // 如果文件还没生成，返回空列表，防止程序崩溃
        }

        try (Reader reader = new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8)) {
            List<T> list = gson.fromJson(reader, typeToken.getType());
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}