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

    @Value("F://Software//develop_tools//Anaconda//envs//py38//python.exe")
    String pythonPath;

    @Value("src/main/java/com/github/herdeny/python/GeneAnalysis.py")
    String GeneAnalysisPath;

    @Value("final_spatial_fusion.csv")
    String final_spatial_fusion;

    @Value("label_mapping.csv")
    String label_mapping;

    @Value("best_model_tempro-spatialfusion_Braak.h5")
    String best_model_tempro_spatialfusion_Braak;

    @Value("best_model_tempro-spatialfusion_CERAD.h5")
    String best_model_tempro_spatialfusion_CERAD;

    @Value("best_model_tempro-spatialfusion_Cogdx.h5")
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

        try {
            Process process = Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            String actionStr;
            if ((actionStr = in.readLine()) != null) {
                System.out.println("Completed Predicting pathological stages");
                return actionStr;
            }

            String errorStr;
            while ((errorStr = err.readLine()) != null) {
                System.err.println(errorStr);
            }

            in.close();
            err.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
