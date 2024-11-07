package com.github.herdeny.service;

/**
 * ClassName: PredictedLabelsService
 * Description:
 * 疾病阶段预测服务层
 * @Author Joel
 * @Create 2024/10/3 16:16
 * @Version 1.0
 */


public interface PredictedLabelsService {

    // 预测疾病阶段
    String predict(String filePath, String uid);
}
