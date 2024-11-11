package com.github.herdeny.controller;


import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.Report_Service;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;


/**
 * 报告 模块
 *
 */
@RestController
@RequestMapping("/report")
public class Report_Controller extends CommonController {

    @Autowired
    private Report_Service report_service;

    @Value("${DATA_PATH}")
    private String DATA_PATH;


    /**
     * 生成报告
     * 成功生成报告的前提是，服务器同步后端2024-11-11版本更新后进行至少进行过一次AD-GRN的绘制，以及疾病阶段预测
     * @param uid 用于指定SSE发送端口
     */
    @GetMapping("/create")
    public Result<Map<String,Object>> createReport(String uid) {
        JSONObject result = report_service.createReport(uid);
        if (result.getBoolean("success")) {
            return Result.success(result.toMap());
        }
        return Result.error(result.getInt("Error code"), "报告生成失败", result.toMap());
    }


    /**
     * 获取报告
     *
     * @param response HttpServletResponse
     * @param mode     获取模式，可选{0,1}，0为获取样例报告，1为获取真实报告，默认为0
     * @return Raw
     */
    @GetMapping("/get")
    public void getGRN(HttpServletResponse response, @RequestParam(defaultValue = "0") String mode) {
        String ReportPath = DATA_PATH + (mode.equals("0") ? "report_sample.pdf" : "pathology_report.pdf");
        // 判断路径是否存在
        if (!new File(ReportPath).exists()) {
            return;
        }
        response.setContentType("application/pdf");
        readFile(response, ReportPath);
    }
}
