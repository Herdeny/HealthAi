package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.PredictedLabelsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * 阶段预测 模块
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

    @Value("${TEST_DATA_PATH}")
    private String TEST_DATA_PATH;

    System.Logger logger = System.getLogger(PredictedLabelsController.class.getName());

    /**
     * 运行预测
     * 调用用户上传的 csv 文件进行预测
     *
     * @param fileName 上传的文件名
     * @param uid      用于指定SSE发送端口
     * @return {
     * "code": 0,
     * "msg": "success",
     * "data": "2"
     * }
     */
    @PostMapping("/run")
    public Result<String> run(@RequestParam String fileName, String uid) {
        String result = predictedLabelsService.predict(DATA_PATH + fileName, uid);
        return Result.success(result);
    }

    /**
     * 获取测试集文件列表
     *
     * @param test_code 测试数据集编号, 可选{1}
     * @return {
     * "code": 0,
     * "msg": "success",
     * "data": [
     * "ACT_377_4830.csv",
     * "ACT_377_4830.csv",
     * "ACT_377_4830.csv"
     * ]
     * }
     */
    @GetMapping("/getFileNames")
    public Result<String[]> getFileNames(@RequestParam String test_code) {
        // 创建一个 File 对象以指向 TEST_DATA1_PATH 路径
        File directory = new File(TEST_DATA_PATH + test_code);
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

