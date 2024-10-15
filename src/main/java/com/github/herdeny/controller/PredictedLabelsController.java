package com.github.herdeny.controller;

import com.github.herdeny.pojo.Query;
import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.PredictedLabelsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * ClassName: PredictedLabelsController
 * Description:
 * 疾病阶段预测控制层
 * @Author Joel
 * @Create 2024/10/3 16:09
 * @Version 1.0
 */

@RestController
@RequestMapping("/prediction")
public class PredictedLabelsController {
    @Resource
    private PredictedLabelsService predictedLabelsService;

    System.Logger logger = System.getLogger(PredictedLabelsController.class.getName());

    @PostMapping("/run")
    public Result run(@RequestParam String filePath) {
        String result = predictedLabelsService.predict(filePath);
        return Result.success("It is predicted that the patient is currently in the disease stage"+result);
    }

 /*   @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestBody Query query) {
        String filePath = query.getData();
        SseEmitter sseEmitter = new SseEmitter();
        Executors.newSingleThreadExecutor().execute(() -> {
            Process process = null;
            try {
                String[] args1 = new String[]{
                        pythonPath, GeneAnalysisPath, filePath,
                        final_spatial_fusion, label_mapping,
                        best_model_tempro_spatialfusion_Braak,
                        best_model_tempro_spatialfusion_CERAD,
                        best_model_tempro_spatialfusion_Cogdx
                };
                process = Runtime.getRuntime().exec(args1);
                // 读取进程的标准输出流
                try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line); // 输出到控制台
                        sseEmitter.send(line);     // 发送到前端
                    }
                }// 等待进程结束
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    sseEmitter.completeWithError(new RuntimeException("Process exited with code: " + exitCode));
                }

                sseEmitter.complete(); // 完成发送
            } catch (Exception e) {
                try {
                    sseEmitter.completeWithError(e); // 发送错误到前端
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } finally {
                if (process != null) {
                    process.destroy(); // 清理进程
                }
            }
        });
        return sseEmitter;
    }*/

}
