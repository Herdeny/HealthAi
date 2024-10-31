package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ADGRN_Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/adgrn")
public class ADGRN_Controller extends CommonController {

    @Autowired
    private ADGRN_Service adgrnService;

    @Value("${DATA_PATH}")
    private String DATA_PATH;

    System.Logger logger = System.getLogger(ADGRN_Controller.class.getName());


    @PostMapping("/test")
    public Result Test(@RequestParam String uid) {
        adgrnService.adgrn_test(uid);
        return Result.success("ok");
    }

    @PostMapping("/run")
    public Result run(@RequestParam String fileName, String uid) {
        String filePath = DATA_PATH + fileName;
        String loom_filePath = adgrnService.adgrn_createLoom(filePath, uid);
        adgrnService.adgrn_createTSV(loom_filePath, uid);
        adgrnService.adgrn_createImg("adj.tsv", uid);
        return Result.success("complete create adj.tsv");
    }


    @PostMapping("/loom")
    public Result loom(@RequestParam String filePath, String uid) {
        String loom_filePath = adgrnService.adgrn_createLoom(filePath, uid);
        return Result.success(loom_filePath);
    }

    @PostMapping("/tsv")
    public Result tsv(@RequestParam String filePath, String uid) {
        int returnCode = adgrnService.adgrn_createTSV(filePath, uid);
        if (returnCode != 0) {
            return Result.error(returnCode, "TSV生成失败");
        }
        return Result.success(returnCode);
    }


    @PostMapping("/img")
    public Result img(@RequestParam String filePath, String uid) {
        adgrnService.adgrn_createImg(filePath, uid);
        return Result.success();
    }

    /**
     * 获取GRN图
     *
     * @param response
     */
    @GetMapping("/getGRN")
    public void getAvatar(HttpServletResponse response) {
        String GRNFolderPath = DATA_PATH + "GRN.png";
        // 判断GRN路径是否存在
        while (!new File(GRNFolderPath).exists()) {
            return;
        }
        response.setContentType("image/png");
        readFile(response, GRNFolderPath);
    }

}
