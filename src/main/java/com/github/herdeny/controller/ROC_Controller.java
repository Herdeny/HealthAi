package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ROC_Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;


@RestController
@RequestMapping("/ROC")
public class ROC_Controller extends CommonController {

    @Autowired
    private ROC_Service rocService;

    @Value("${DATA_PATH}")
    private String DATA_PATH;

    @Value("${MODEL_PATH}")
    private String MODEL_PATH;

    System.Logger logger = System.getLogger(PredictedLabelsController.class.getName());


    //TODO 返回值
    @PostMapping("/test")
    public Result test(@RequestParam String test_code) {
        String result = rocService.test(test_code);
        return Result.success("test completed" + result);
    }


    /**
     * 获取ROC图
     *
     * @param response
     */
    @GetMapping("/getROC")
    public void getROC(HttpServletResponse response) {
        String ROCFolderPath = DATA_PATH + "ROC.png";
        // 判断路径是否存在
        if (!new File(ROCFolderPath).exists()) {
            return;
        }
        response.setContentType("image/png");
        readFile(response, ROCFolderPath);
    }
}
