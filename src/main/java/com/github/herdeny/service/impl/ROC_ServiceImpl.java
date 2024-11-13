package com.github.herdeny.service.impl;

import com.github.herdeny.service.ROC_Service;
import com.github.herdeny.utils.SseClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ROC_ServiceImpl implements ROC_Service {

    private final SseClient sseClient;
    @Value("${PYTHON_PATH}")
    private String pythonPath;

    @Value("${CREATE_ROC_PATH}")
    private String createROCPath;

    @Value("${MODEL_PATH}")
    private String modelPath;

    @Value("${DATA_PATH}")
    private String dataPath;

    public ROC_ServiceImpl(SseClient sseClient) {
        this.sseClient = sseClient;
    }

    @Override
    public JSONObject test(String test_code, String uid) {
        JSONObject[] result = new JSONObject[1];
        result[0] = new JSONObject();
        boolean[] flag = {true};

        System.out.println("Start generate ROC...");
        sseClient.sendMessage(uid, uid + "-start-create-roc", "Start generate ROC...");
        String[] args1 = new String[]{pythonPath, createROCPath, modelPath, dataPath, test_code};

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(args1);

            // 创建两个线程分别处理标准输出和错误输出
            Process finalProcess = process;
            Thread outputThread = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(finalProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                        if (line.contains("s/step")) {
                            continue;
                        }
                        String MessageID = UUID.randomUUID().toString();
                        sseClient.sendMessage(uid, uid + "-" + MessageID, line);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading process output: " + e.getMessage());
                }
            });

            Process finalProcess1 = process;
            Thread errorThread = new Thread(() -> {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(finalProcess1.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorStr;
                    while ((errorStr = err.readLine()) != null) {
                        if (errorStr.contains("Error") && !errorStr.startsWith("WARNING")) {
                            if (flag[0]) flag[0] = false;
                            String regex = "\\[(Errno|WinError)\\s+(\\d+)]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(errorStr);
                            if (matcher.find()) {
                                result[0].put("code", matcher.group(2));
                            }
                            result[0].put("data", errorStr);
                            sseClient.sendMessage(uid, uid + "-error-create-roc", "create roc error");
                        }
                        System.err.println(errorStr);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading process error output: " + e.getMessage());
                }
            });

            outputThread.start();
            errorThread.start();

            // 等待进程完成并确保输出线程正常结束
            int exitCode = process.waitFor();
            outputThread.join();
            errorThread.join();

            result[0].put("success", flag[0]);
            if (exitCode == 0) {
                result[0].put("code", 0);
                System.out.println("Completed Generate ROC stages");
            }
            return result[0];

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during prediction: " + e.getMessage(), e);
        } finally {
            if (process != null) {
                process.destroy(); // 确保清理进程
            }
        }
    }
}
