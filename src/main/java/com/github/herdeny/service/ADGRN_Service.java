package com.github.herdeny.service;

import org.json.JSONObject;

public interface ADGRN_Service {
    boolean adgrn_createLoom(String filePath, String uid);

    JSONObject adgrn_createImg(String filePath, String uid);

    int adgrn_createTSV(String filePath, String uid);
}
