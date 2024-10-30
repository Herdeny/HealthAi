package com.github.herdeny.service.impl;

import com.github.herdeny.service.ADGRN_Service;
import com.github.herdeny.utils.SseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Service
public class ADGRN_ServiceImpl implements ADGRN_Service {

    @Value("${MODEL_PATH}")
    String MODEL_PATH;

    @Value("${DATA_PATH}")
    String DATA_PATH;

    @Value("${PYTHON_PATH}")
    String pythonPath;

    @Value("${PYSCENIC_PATH}")
    String pyScenicPath;

    @Value("${CREATE_LOOM_PATH}")
    String CREATE_LOOM_PATH;

    @Value("${CREATE_IMG_PATH}")
    String CREATE_IMG_PATH;

    @Value("${CREATE_ADGRN_COMPLEX_PATH}")
    String CREATE_ADGRN_COMPLEX_PATH;

    @Value("${hg38}")
    String hg38_PATH;

    @Value("${hs_hgnc_tfs}")
    String hs_hgnc_tfs_PATH;

    @Value("${motifs_PATH}")
    String motifs_PATH;

    @Autowired
    private SseClient sseClient;

    @Override
    public String adgrn_createLoom(String filePath) {
        System.out.println("Start Generate Loom...");

        String[] args1 = new String[]{pythonPath, CREATE_LOOM_PATH, filePath};

        try {
            Process process = Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            String actionStr;
            if ((actionStr = in.readLine()) != null) {
                System.out.println("Completed Generate Loom");
                return actionStr;
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
        return null;
    }

    @Override
    public void adgrn_createImg(String filePath) {
        System.out.println("Start Generate Image...");

        String[] args1 = new String[]{pythonPath, CREATE_IMG_PATH, filePath};

        try {
            Process process = Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            String actionStr;
            while ((actionStr = in.readLine()) != null) {
                System.out.println(actionStr);
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
    }

    public int adgrn_createTSV(String filePath) {
        System.out.println("Start Generate TSV...");
        try {
            // Build the command
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pyScenicPath, "grn",
                    "--num_workers", "5",
                    "--output", "adj.tsv",
                    "--method", "grnboost2",
                    filePath, hs_hgnc_tfs_PATH
            );

            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

            if (exitCode != 0) {
                System.out.println("Failed to generate TSV");
                return exitCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Complete Generate TSV");
        return 0;
    }

    @Override
    public void adgrn_test(String uid) {
        System.out.println("Test-Start Generate Image...");
        sseClient.sendMessage(uid, "start", "Start Generate Image...");

        String[] args1 = new String[]{pythonPath, CREATE_ADGRN_COMPLEX_PATH, MODEL_PATH, DATA_PATH};

        try {
            Process process = Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            String actionStr;
            while ((actionStr = in.readLine()) != null) {
                System.out.println(actionStr);
                String messageId = UUID.randomUUID().toString();
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
        System.out.println("Test-Complete Generate Image");
        sseClient.sendMessage(uid, "end", "Complete Generate Image");
    }
}

