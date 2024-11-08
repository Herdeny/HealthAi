package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import com.github.herdeny.service.ADGRN_Service;
import com.github.herdeny.utils.JsonTools;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

/**
 * AD-GRN 模块
 */
@RestController
@RequestMapping("/adgrn")
public class ADGRN_Controller extends CommonController {

    @Autowired
    private ADGRN_Service adgrnService;

    @Value("${DATA_PATH}")
    private String DATA_PATH;

    /**
     * 完整测试
     * 调用数据集中已存在的 `ACT_377_4830.csv` 文件进行测试
     * @param uid 用于指定SSE发送端口
     * @return
     * {
     *     "code": 0,
     *     "msg": "success",
     *     "data": {
     *         "网络图边数量": "2901",
     *         "模块数量": "44",
     *         "网络图节点数量": "4830"
     *     }
     * }
     */
    @PostMapping("/test")
    public Result<Map<String, Object>> Test(String uid) {
        return run("ACT_377_4830.csv", uid);
    }


    /**
     * 完整运行
     * 调用用户上传的 csv 文件进行绘图
     * @param fileName 上传的文件名
     * @param uid 用于指定SSE发送端口
     * @return
     * {
     *     "code": 0,
     *     "msg": "success",
     *     "data": {
     *         "网络图边数量": "2901",
     *         "模块数量": "44",
     *         "网络图节点数量": "4830"
     *     }
     * }
     */
    @PostMapping("/run")
    public Result<Map<String, Object>> run(@RequestParam String fileName, String uid) {
        String filePath = DATA_PATH + fileName;
        if (!adgrnService.adgrn_createLoom(filePath, uid)) {
            return Result.error(500, "loom文件生成失败");
        }
        String loom_filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".loom";
        if (adgrnService.adgrn_createTSV(loom_filePath, uid) != 0) {
            return Result.error(500, "TSV生成失败");
        }
        JSONObject result_json = adgrnService.adgrn_createImg("adj.tsv", uid);
        if (result_json.isEmpty()) {
            return Result.error(500, "绘图数据为空");
        }
        System.out.println("返回绘图数据文件：" + result_json);
        JsonTools jsonTools = new JsonTools();
        jsonTools.saveJsonToFile(result_json, DATA_PATH + "result.json");
        System.out.println("result.json saved");
        // 将 JSONObject 转换为 Map
        Map<String, Object> map = result_json.toMap();
        return Result.success(map);
    }

    /**
     * 生成 loom 文件
     * 调用用户上传的 csv 文件生成 loom 文件
     * @param filePath csv 文件路径
     * @param uid 用于指定SSE发送端口
     * @return
     * {
     *     "code": 0,
     *     "msg": "success",
     *     "data": "loom文件生成成功"
     * }
     */
    @PostMapping("/loom")
    public Result<String> loom(@RequestParam String filePath, String uid) {
        boolean flag = adgrnService.adgrn_createLoom(filePath, uid);
        if (!flag)
            return Result.error(500, "loom文件生成失败");
        return Result.success("loom文件生成成功");
    }

    /**
     * 生成 TSV 文件
     * 调用用户上传的 loom 文件生成 TSV 文件
     * @param filePath loom 文件路径
     * @param uid 用于指定SSE发送端口
     * @return
     * {
     *     "code": 0,
     *     "msg": "success",
     *     "data": 0
     * }
     */
    @PostMapping("/tsv")
    public Result<Integer> tsv(@RequestParam String filePath, String uid) {
        int returnCode = adgrnService.adgrn_createTSV(filePath, uid);
        if (returnCode != 0) {
            return Result.error(returnCode, "TSV生成失败");
        }
        return Result.success(returnCode);
    }

    /**
     * 生成 AD-GRN 图
     * 调用用户上传的 TSV 文件生成图片
     * @param filePath TSV 文件路径
     * @param uid 用于指定SSE发送端口
     * @return
     * {
     *     "code": 0,
     *     "msg": "success",
     *     "data": {
     *         "网络图边数量": "2901",
     *         "模块数量": "44",
     *         "网络图节点数量": "4830"
     *     }
     * }
     */
    @PostMapping("/img")
    public Result<Map<String, Object>> img(@RequestParam String filePath, String uid) {
        JSONObject result_json = adgrnService.adgrn_createImg(filePath, uid);
        if (result_json.isEmpty()) {
            return Result.error(500, "绘图数据为空");
        }
        System.out.println(result_json);

        // 将 JSONObject 转换为 Map
        Map<String, Object> map = result_json.toMap();
        return Result.success(map);
    }

    /**
     * 获取GRN图
     *
     * @param response
     * @return Raw image/png
     */
    @GetMapping("/getGRN")
    public void getGRN(HttpServletResponse response) {
        String GRNFolderPath = DATA_PATH + "GRN.png";
        // 判断GRN路径是否存在
        if (!new File(GRNFolderPath).exists()) {
            return;
        }
        response.setContentType("image/png");
        readFile(response, GRNFolderPath);
    }

}
