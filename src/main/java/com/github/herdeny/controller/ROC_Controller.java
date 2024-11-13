package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ROC_Service;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

/**
 * ROC 模块
 * 用于 ROC 图的绘制
 */
@RestController
@RequestMapping("/ROC")
public class ROC_Controller extends CommonController {

    @Autowired
    private ROC_Service rocService;

    @Value("${DATA_PATH}")
    private String DATA_PATH;

    /**
     * 测试绘图
     *
     * @param test_code 测试数据集编号，可选{1}
     * @param uid       用于指定SSE发送端口
     */
    @PostMapping("/test")
    public Result<Map<String, Object>> test(@RequestParam String test_code, String uid) {
        JSONObject result = rocService.test(test_code, uid);
        if (!result.getBoolean("success")) {
            return Result.error(result.getInt("code"), "ROC绘制失败", result.toMap());
        }
        return Result.success(result.toMap());
    }


    /**
     * 获取ROC图
     *
     * @param response
     * @return Raw image/png
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
