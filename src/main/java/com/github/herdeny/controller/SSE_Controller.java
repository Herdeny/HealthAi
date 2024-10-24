package com.github.herdeny.controller;

import cn.hutool.core.util.IdUtil;
import com.github.herdeny.utils.SseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("/sse")
public class SSE_Controller {
    @Autowired
    private SseClient sseClient;
    @GetMapping("/index")
    public String index(ModelMap model) {
        String uid = IdUtil.fastUUID();
        model.put("uid",uid);
        return "index";
    }

    @CrossOrigin
    @GetMapping("/createSse")
    public SseEmitter createConnect(String uid) {
        return sseClient.createSse(uid);
    }


    @CrossOrigin
    @GetMapping("/sendMsg")
    @ResponseBody
    public String sseChat(String uid) {
        for (int i = 0; i < 10; i++) {
            sseClient.sendMessage(uid, "no"+i, "Hello World");
        }
        return "ok";
    }

    /**
     * 关闭连接
     */
    @CrossOrigin
    @GetMapping("/closeSse")
    public void closeConnect(String uid ){

        sseClient.closeSse(uid);
    }
}