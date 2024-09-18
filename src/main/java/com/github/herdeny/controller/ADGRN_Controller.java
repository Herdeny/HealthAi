package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ADGRN_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adgrn")
public class ADGRN_Controller {
    @Autowired
    private ADGRN_Service adgrnService;

    System.Logger logger = System.getLogger(ADGRN_Controller.class.getName());

    @PostMapping("/test")
    public Result Test() {
        logger.log(System.Logger.Level.INFO, "Test");
        return Result.success();
    }
    @PostMapping("/run")
    public Result run(String filePath) {
        //filePath = "D://ACM//HealthAI//ACT_377_4830.csv";
        String loom_filePath = adgrnService.adgrn_createLoom(filePath);
        adgrnService.adgrn_createTSV(loom_filePath);
        adgrnService.adgrn_createImg("adj.tsv");
        return Result.success(loom_filePath);
    }

    @PostMapping("/loom")
    public Result loom(String filePath) {
        //filePath = "D://ACM//HealthAI//ACT_377_4830.csv";
        String loom_filePath = adgrnService.adgrn_createLoom(filePath);
        return Result.success(loom_filePath);
    }

    @PostMapping("/tsv")
    public Result tsv(String filePath) {
        //filePath = "D://ACM//HealthAI//ACT_377_4830.loom";
        int returnCode = adgrnService.adgrn_createTSV(filePath);
        if (returnCode != 0) {
            return Result.error(returnCode,"TSV生成失败");
        }
        return Result.success(returnCode);
    }


    @PostMapping("/img")
    public Result img(String filePath) {
        //filePath = "D://ACM//HealthAI//adj.tsv";
        adgrnService.adgrn_createImg(filePath);
        return Result.success();
    }

}
