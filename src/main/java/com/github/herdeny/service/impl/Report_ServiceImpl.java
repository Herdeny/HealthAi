package com.github.herdeny.service.impl;

import com.github.herdeny.service.Report_Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//TODO 报告生成
@Service
public class Report_ServiceImpl implements Report_Service {
    @Value("${CREATE_REPORT_PATH}")
    private String CREATE_REPORT_PATH;
}
