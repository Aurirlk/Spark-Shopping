package com.shopping.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final com.shopping.config.MinioConfig minioConfig;

    /**
     * 上传文件
     */
    public String upload(MultipartFile file, String folder) {
        try {
            String bucket = minioConfig.getBucket();
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());

            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = folder + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket).object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return minioConfig.getEndpoint() + "/" + bucket + "/" + fileName;
        } catch (Exception e) {
            log.error("MinIO上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    /** 上传图片到 images 目录 */
    public String uploadImage(MultipartFile file) { return upload(file, "images"); }

    /** 获取临时访问URL（7天有效） */
    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucket()).object(objectName)
                    .method(Method.GET).expiry(7, TimeUnit.DAYS).build());
        } catch (Exception e) { return null; }
    }

    /** 删除文件 */
    public void delete(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucket()).object(objectName).build());
        } catch (Exception e) { log.error("MinIO删除失败", e); }
    }
}
