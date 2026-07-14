package org.example.service.impl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置属性 Bean。
 *
 * <p>前缀 aliyun.oss，映射 application.yml 中的 OSS 配置项。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSProperties {
    /** OSS Endpoint 域名（如 https://oss-cn-beijing.aliyuncs.com） */
    private String endpoint;
    /** Bucket 名称 */
    private String bucketName;
    /** OSS 区域（如 cn-beijing） */
    private String region;
    /** AccessKey ID（从环境变量注入） */
    private String accessKeyId;
    /** AccessKey Secret（从环境变量注入） */
    private String accessKeySecret;
}
