package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ROC_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ROC")
public class ROC_Controller {

    @Autowired
    private ROC_Service rocService;

    @Value("${DATA_PATH}")
    private static String DATA_PATH;

    @Value("${MODEL_PATH}")
    private static String MODEL_PATH;

    System.Logger logger = System.getLogger(PredictedLabelsController.class.getName());

    @PostMapping("/test")
    public Result test() {
        String result = rocService.test();
        return Result.success("test completed" + result);
    }
}
