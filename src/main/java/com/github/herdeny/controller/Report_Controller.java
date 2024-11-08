package com.github.herdeny.controller;


import com.github.herdeny.service.Report_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO 报告生成接口
@RestController
@RequestMapping("/Report")
public class Report_Controller {

    @Autowired
    private Report_Service report_service;

}
