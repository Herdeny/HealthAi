package com.github.herdeny.service.impl;

import com.github.herdeny.service.ROC_Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class ROC_ServiceImpl implements ROC_Service {

    @Value("${PYTHON_PATH}")
    private String pythonPath;

    @Value("${CREATE_ROC_PATH}")
    private String createROCPath;

    @Value("${MODEL_PATH}")
    private String modelPath;

    @Value("${DATA_PATH}")
    private String dataPath;

    @Override
    public String test(String test_code) {
        System.out.println("Start generate ROC...");

        String[] args1 = new String[]{pythonPath, createROCPath, modelPath, dataPath, test_code};

        Process process = null;
        StringBuilder outputBuilder = new StringBuilder(); // 用于存储Python输出结果
        try {
            process = Runtime.getRuntime().exec(args1);

            // 创建两个线程分别处理标准输出和错误输出
            Process finalProcess = process;
            Thread outputThread = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(finalProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                        if (line.contains("ms/step")){
                            continue;
                        }
                        outputBuilder.append(line);
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

            if (exitCode == 0) {
                System.out.println("Completed Generate ROC stages");
                return outputBuilder.toString();
            } else {
                System.err.println("Process exited with code: " + exitCode);
                // 可以根据需要返回不同的结果或抛出异常
                return null;
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during prediction: " + e.getMessage(), e);
        } finally {
            if (process != null) {
                process.destroy(); // 确保清理进程
            }
        }
    }
}
