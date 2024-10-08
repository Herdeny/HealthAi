package com.github.herdeny.controller;

import com.github.herdeny.pojo.Query;
import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.PredictedLabelsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result run(@RequestBody Query query /*String filePath*/) {
        String filePath = query.getData();
        String result = predictedLabelsService.predict(filePath);
        return Result.success("经过预测，该病人当前处在疾病阶段"+result);
    }
}
