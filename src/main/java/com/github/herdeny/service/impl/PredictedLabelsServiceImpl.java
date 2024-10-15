package com.github.herdeny.service.impl;

import com.github.herdeny.service.PredictedLabelsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * ClassName: PredictedLabelsServiceImpl
 * Description:
 *
 * @Author Joel
 * @Create 2024/10/3 16:17
 * @Version 1.0
 */

@Service
public class PredictedLabelsServiceImpl implements PredictedLabelsService {

    @Value("${PYTHON_PATH}")
    String pythonPath;

    @Value("${GeneAnalysisPath}")
    String GeneAnalysisPath;

    @Value("${final_spatial_fusion}")
    String final_spatial_fusion;

    @Value("${label_mapping}")
    String label_mapping;

    @Value("${Braak.h5}")
    String best_model_tempro_spatialfusion_Braak;

    @Value("${CERAD.h5}")
    String best_model_tempro_spatialfusion_CERAD;

    @Value("${Cogdx.h5}")
    String best_model_tempro_spatialfusion_Cogdx;

    /**
     * 疾病阶段预测
     * @param filePath 用户传入的csv文件路径
     */
    @Override
    public String predict(String filePath) {
        System.out.println("Start Predicting pathological stages...");

        String[] args1 = new String[]{pythonPath, GeneAnalysisPath, filePath,
                final_spatial_fusion, label_mapping,
                best_model_tempro_spatialfusion_Braak,
                best_model_tempro_spatialfusion_CERAD,
                best_model_tempro_spatialfusion_Cogdx
        };

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
                System.out.println("Completed Predicting pathological stages");
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
