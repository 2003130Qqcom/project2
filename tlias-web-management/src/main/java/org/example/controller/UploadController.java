package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.utils.AliyunOSSOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器。
 *
 * <p>文件上传至阿里云 OSS，返回公网可访问 URL。</p>
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    /**
     * 上传文件到阿里云 OSS
     *
     * <p>请求：POST /upload + multipart/form-data file=xxx</p>
     * <p>响应：{@code Result{data: "https://bucket.endpoint/2026/06/uuid.png"}}</p>
     *
     * @param file 上传文件（表单字段名: file）
     * @return 文件公网 URL
     */
    @PostMapping
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("上传文件，文件名：{}", file.getOriginalFilename());
        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        return Result.success(url);
    }
}
