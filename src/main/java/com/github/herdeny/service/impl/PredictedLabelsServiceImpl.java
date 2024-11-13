package com.github.herdeny.service.impl;

import com.github.herdeny.service.PredictedLabelsService;
import com.github.herdeny.utils.JsonTools;
import com.github.herdeny.utils.SseClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    @Value("${DATA_PATH}")
    String dataPath;

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

    @Autowired
    private SseClient sseClient;

    static final String[] description = {"NO DEMENTIA SEEN", "SUBJECTIVE MEMORY LOSS, AGE RELATED FORGETFULNESS", "MILD COGNITIVE IMPAIRMENT", "MODERATELY SEVERE COGNITIVE DECLINE, MODERATE DEMENTIA", "SEVERE COGNITIVE DECLINE, MODERATELY SEVERE DEMENTIA", "VERY SEVERE COGNITIVE DECLINE, SEVERE DEMENTIA"};

    /**
     * 疾病阶段预测
     *
     * @param filePath 用户传入的csv文件路径
     */
    @Override
    public JSONObject predict(String filePath, String uid) {
        JSONObject[] result = new JSONObject[1];
        result[0] = new JSONObject();
        boolean[] flag = {true};
        System.out.println("Start Predicting pathological stages...");
        sseClient.sendMessage(uid, uid + "-start-predict", "Start Predicting pathological stages...");

        System.out.println(dataPath);
        String[] args1 = new String[]{pythonPath, GeneAnalysisPath, filePath,
                final_spatial_fusion, label_mapping,
                best_model_tempro_spatialfusion_Braak,
                best_model_tempro_spatialfusion_CERAD,
                best_model_tempro_spatialfusion_Cogdx
        };

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
                        String messageID = uid + "-" + UUID.randomUUID();
                        sseClient.sendMessage(uid, messageID, line);
                        if (line.startsWith("Predicted disease stages:")) {
                            result[0].put("data", line.substring(line.lastIndexOf('[') + 1, line.lastIndexOf(']')));
                        }
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
                            sseClient.sendMessage(uid, uid + "-error-predict", "Failed Predicting pathological stages...");
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
                JSONObject result_json = new JSONObject();
                result_json.put("疾病阶段", result[0].getInt("data") + 1);
                result_json.put("描述", description[result[0].getString("data").charAt(0) - '0']);
                JsonTools jsonTools = new JsonTools();
                jsonTools.saveJsonToFile(result_json, dataPath + "Prediction result.json");
                System.out.println("Prediction result.json saved");
            } else {
                System.err.println("Process exited with code: " + exitCode);
                // 可以根据需要返回不同的结果或抛出异常
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
