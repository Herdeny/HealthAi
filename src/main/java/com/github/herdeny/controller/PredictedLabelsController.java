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
 *
 * @Author Joel
 * @Create 2024/10/3 16:09
 * @Version 1.0
 */

@RestController
@RequestMapping("/prediction")
public class PredictedLabelsController {

    @Resource
    private PredictedLabelsService predictedLabelsService;

    @Value("${DATA_PATH}")
    private String DIRECTORY;

    System.Logger logger = System.getLogger(PredictedLabelsController.class.getName());

    @PostMapping("/run")
    public Result run(@RequestParam String fileName) {
        String result = predictedLabelsService.predict(fileName);
        return Result.success(result);
    }

}
