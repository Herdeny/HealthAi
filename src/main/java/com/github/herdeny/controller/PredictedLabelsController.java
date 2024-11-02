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
import java.io.File;
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
    private String DATA_PATH;

    @Value("${TEST_DATA1_PATH}")
    private String TEST_DATA1_PATH;

    @Value("${TEST_DATA2_PATH}")
    private String TEST_DATA2_PATH;

    System.Logger logger = System.getLogger(PredictedLabelsController.class.getName());

    @PostMapping("/run")
    public Result run(@RequestParam String fileName) {
        String result = predictedLabelsService.predict(DATA_PATH + fileName);
        return Result.success(result);
    }

    @GetMapping("/test1/name")
    public Result<String[]> getTest1Name(){
        // 创建一个 File 对象以指向 TEST_DATA1_PATH 路径
        File directory = new File(TEST_DATA1_PATH);
        // 检查路径是否存在且是一个目录
        if (!directory.exists() || !directory.isDirectory()) {
            return Result.error("路径不存在或不是一个目录");
        }
        // 获取目录下的所有文件名
        String[] fileNames = directory.list((dir, name) -> new File(dir, name).isFile());
        // 检查是否成功读取文件名
        if (fileNames == null) {
            return Result.error("无法读取文件名");
        }
        return Result.success(fileNames);
    }

    @GetMapping("/test2/name")
    public Result<String[]> getTest2Name(){
        // 创建一个 File 对象以指向 TEST_DATA2_PATH 路径
        File directory = new File(TEST_DATA2_PATH);
        // 检查路径是否存在且是一个目录
        if (!directory.exists() || !directory.isDirectory()) {
            return Result.error("路径不存在或不是一个目录");
        }
        // 获取目录下的所有文件名
        String[] fileNames = directory.list((dir, name) -> new File(dir, name).isFile());
        // 检查是否成功读取文件名
        if (fileNames == null) {
            return Result.error("无法读取文件名");
        }
        return Result.success(fileNames);
    }
}
