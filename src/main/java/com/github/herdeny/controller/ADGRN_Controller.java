package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ADGRN_Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/adgrn")
public class ADGRN_Controller extends CommonController{
    @Autowired
    private ADGRN_Service adgrnService;

    private static final String DIRECTORY = "/path/on/server/data/";

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

    /**
     * 获取GRN图
     * @param response
     */
    @GetMapping("/getGRN")
    public void getAvatar(HttpServletResponse response) {
        String GRNFolderPath = DIRECTORY + "GRN.png";
        // 判断用户头像是否存在，如果不存在就使用默认头像
        while (!new File(GRNFolderPath).exists()) {
            return;
        }
        response.setContentType("image/jpeg");
        readFile(response, GRNFolderPath);
    }

}
