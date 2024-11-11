package com.github.herdeny.service.impl;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ADGRN_Service;
import com.github.herdeny.utils.SseClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class ADGRN_ServiceImpl implements ADGRN_Service {

    @Value("${MODEL_PATH}")
    String MODEL_PATH;

    @Value("${DATA_PATH}")
    String DATA_PATH;

    @Value("${PYTHON_PATH}")
    String PYTHON_PATH;

    @Value("${PYSCENIC_PATH}")
    String pyScenicPath;

    @Value("${CREATE_LOOM_PATH}")
    String CREATE_LOOM_PATH;

    @Value("${CREATE_ADGRN_COMPLEX_PATH}")
    String CREATE_ADGRN_COMPLEX_PATH;

    @Value("${hs_hgnc_tfs}")
    String hs_hgnc_tfs_PATH;

    @Autowired
    private SseClient sseClient;

    @Override
    public boolean adgrn_createLoom(String filePath, String uid) {
        boolean flag = true;
        System.out.println("Start Generate Loom...");
        sseClient.sendMessage(uid, uid + "-start-create-loom", "Start Generate Loom...");

        String[] args = new String[]{PYTHON_PATH, CREATE_LOOM_PATH, filePath};

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
                if (flag) flag = false;
                System.err.println(errorStr);
            }

            in.close();
            err.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    public JSONObject adgrn_createTSV(String filePath, String uid) {
        boolean flag = true;
        JSONObject result = new JSONObject();
        System.out.println("Start Generate TSV...");
        sseClient.sendMessage(uid, uid + "-start-create-tsv", "Start Generate TSV...");


        String[] args = new String[]{
                pyScenicPath, "grn",
                "--num_workers", "5",
                "--output", DATA_PATH + "adj.tsv",
                "--method", "grnboost2",
                filePath, hs_hgnc_tfs_PATH
        };

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
                if (errorStr.contains("error:")) {
                    if (flag) flag = false;
                    String regex = "\\[Errno (\\d+)]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(errorStr);
                    if (matcher.find()) {
                        result.put("Error code", matcher.group(1));
                    }
                    result.put("Error Message", errorStr.substring(errorStr.indexOf("error:") + 7));
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
            System.out.println("Complete Generate TSV");
            sseClient.sendMessage(uid, uid + "-end-create-tsv", "Complete Generate TSV");
            result.put("code", 0);
        }
        return result;
    }

    @Override
    public JSONObject adgrn_createImg(String filePath, String uid) {
        JSONObject result = new JSONObject();

        System.out.println("Start Generate Image...");
        sseClient.sendMessage(uid, uid + "-start-adgrn", "Start Generate Image...");

        String[] args1 = new String[]{PYTHON_PATH, CREATE_ADGRN_COMPLEX_PATH, MODEL_PATH, DATA_PATH, filePath};

        try {
            Process process = Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            String actionStr;
            while ((actionStr = in.readLine()) != null) {
                System.out.println(actionStr);
                if (actionStr.startsWith("Number of nodes in the network graph:")) {
                    result.put("网络图节点数量", actionStr.split(":")[1].trim());
                }
                if (actionStr.startsWith("Number of edges in the network graph:")) {
                    result.put("网络图边数量", actionStr.split(":")[1].trim());
                }
                if (actionStr.startsWith("Number of modules:")) {
                    result.put("模块数量", actionStr.split(":")[1].trim());
                }
                String messageId = uid + "-" + UUID.randomUUID();
                sseClient.sendMessage(uid, messageId, actionStr);
            }

            String errorStr;
            while ((errorStr = err.readLine()) != null) {
                System.err.println(errorStr);
            }
            in.close();
            err.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Complete Generate Image");
        sseClient.sendMessage(uid, uid + "-end-create-image", "Complete Generate Image");
        return result;
    }
}

