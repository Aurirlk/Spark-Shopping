package com.shopping.controller;

import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.url:http://localhost:8080/api/uploads}")
    private String uploadUrl;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        try {
            // 生成文件名
            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 按日期分目录
            String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String dirPath = uploadPath + "/images/" + dateDir;
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();

            // 保存文件
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            // 返回访问URL
            String url = uploadUrl + "/images/" + dateDir + "/" + fileName;

            Map<String, Object> data = new HashMap<>();
            data.put("url", url);
            data.put("fileName", fileName);
            data.put("originalName", originalName);
            data.put("size", file.getSize());

            return Result.success(data);
        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 多文件上传
     */
    @PostMapping("/images")
    public Result<java.util.List<Map<String, Object>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        java.util.List<Map<String, Object>> results = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            Result<Map<String, Object>> result = uploadImage(file);
            if (result.getCode() == 200) {
                results.add(result.getData());
            }
        }
        return Result.success(results);
    }
}
