package com.github.herdeny.utils;

import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

public class JsonTools {

    public void saveJsonToFile(JSONObject jsonObject, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonObject.toString()); // 格式化输出
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
