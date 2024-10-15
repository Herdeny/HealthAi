package com.github.herdeny.controller;

import com.github.herdeny.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * ClassName: CommonController
 * Description:
 *
 * @Author Joel
 * @Create 2024/10/15 16:42
 * @Version 1.0
 */

@RestController
public class CommonController {

    private static final String DIRECTORY = "./data/";

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file){
        //检查文件是否为空
        if(file.isEmpty()){
            return Result.error("文件为空");
        }
        //检查目标文件夹是否存在，如果目标文件夹不存在，则创建
        File directory = new File(DIRECTORY);
        if(!directory.exists()){
            directory.mkdir();
        }
        //保存文件
        try {
            File targetFile = new File(directory, file.getOriginalFilename());
            file.transferTo(targetFile);
            return Result.success("文件上传成功："+ targetFile.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("上传文件时出现问题：" + e.getMessage());
        }
    }
}
