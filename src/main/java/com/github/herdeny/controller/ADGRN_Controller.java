package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ADGRN_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adgrn")
public class ADGRN_Controller {
    @Autowired
    private ADGRN_Service adgrnService;

    @Value("${PROJECT_PATH}")
    private static String DIRECTORY;

    System.Logger logger = System.getLogger(ADGRN_Controller.class.getName());

    /**
     * 测试接口
     * @param fileName
     * @return
     */
    @PostMapping("/test")
    public Result Test(@RequestParam String fileName) {
        logger.log(System.Logger.Level.INFO, "Test");
        return Result.success(fileName);
    }

    @PostMapping("/run")
    public Result run(@RequestParam String fileName) {
        String filePath = DIRECTORY + fileName;
        String loom_filePath = adgrnService.adgrn_createLoom(filePath);
        adgrnService.adgrn_createTSV(loom_filePath);
        adgrnService.adgrn_createImg("adj.tsv");
        return Result.success(null);
    }

    @PostMapping("/loom")
    public Result loom(String filePath) {
        String loom_filePath = adgrnService.adgrn_createLoom(filePath);
        return Result.success(loom_filePath);
    }

    @PostMapping("/tsv")
    public Result tsv(String filePath) {
        int returnCode = adgrnService.adgrn_createTSV(filePath);
        if (returnCode != 0) {
            return Result.error(returnCode,"TSV生成失败");
        }
        return Result.success(returnCode);
    }


    @PostMapping("/img")
    public Result img(String filePath) {
        adgrnService.adgrn_createImg(filePath);
        return Result.success();
    }

}
