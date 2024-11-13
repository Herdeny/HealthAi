package com.github.herdeny.service.impl;

import com.github.herdeny.service.Report_Service;
import com.github.herdeny.utils.SseClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO 报告生成
@Service
public class Report_ServiceImpl implements Report_Service {
    @Value("${PYTHON_PATH}")
    private String PYTHON_PATH;

    @Value("${CREATE_REPORT_PATH}")
    private String CREATE_REPORT_PATH;

    @Value("${DATA_PATH}")
    private String DATA_PATH;

    @Value("${MODEL_PATH}")
    private String MODEL_PATH;

    @Autowired
    private SseClient sseClient;

    @Override
    public JSONObject createReport(String uid) {
        JSONObject result = new JSONObject();
        boolean flag = true;
        System.out.println("Start Generate Report...");
        sseClient.sendMessage(uid, uid + "-start-create-report", "Start Generate Report...");
        String[] args = new String[]{PYTHON_PATH, CREATE_REPORT_PATH, MODEL_PATH, DATA_PATH};
        try {
            Process process = Runtime.getRuntime().exec(args);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            // Read the output
            String actionStr;
            while ((actionStr = in.readLine()) != null) {
                System.out.println(actionStr);
                String messageID = uid + "-" + UUID.randomUUID();
                sseClient.sendMessage(uid, messageID, actionStr);
            }

            String errorStr;
            while ((errorStr = err.readLine()) != null) {
                if (errorStr.contains("Error")) {
                    if (flag) flag = false;
                    String regex = "\\[(Errno|WinError)\\s+(\\d+)]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(errorStr);
                    if (matcher.find()) {
                        result.put("code", matcher.group(2));
                    }
                    result.put("data", errorStr);
                    sseClient.sendMessage(uid, uid + "-error-create-report", "create report error");
                }
                System.err.println(errorStr);
            }
            in.close();
            err.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        result.put("success", flag);
        if (flag) {
            sseClient.sendMessage(uid, uid + "-complete-create-report", "Complete Create Report...");
            result.put("code", 0);
        }
        return result;
    }
}
