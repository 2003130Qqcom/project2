package org.example.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import lombok.extern.slf4j.Slf4j;
import org.example.service.impl.AliyunOSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云 OSS 上传工具类。
 *
 * <p>文件最终路径：{bucket}/{yyyy/MM}/{UUID}.{ext}</p>
 */
@Slf4j
@Component
public class AliyunOSSOperator {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    /**
     * 上传文件到阿里云 OSS
     *
     * @param content          文件字节数组
     * @param originalFilename 原始文件名（用于提取扩展名）
     * @return 文件公网访问 URL
     */
    public String upload(byte[] content, String originalFilename) throws Exception {
        // ❶ 准备 OSS 参数
        DefaultCredentialProvider credentials = new DefaultCredentialProvider(
                aliyunOSSProperties.getAccessKeyId(),
                aliyunOSSProperties.getAccessKeySecret());

        // ❷ 构造存储路径：yyyy/MM/UUID.ext
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String ext = "";
        int dot = originalFilename.lastIndexOf(".");
        if (dot > 0) ext = originalFilename.substring(dot);
        String objectName = dir + "/" + UUID.randomUUID() + ext;

        // ❸ 创建客户端并上传
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        config.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(aliyunOSSProperties.getEndpoint())
                .credentialsProvider(credentials)
                .clientConfiguration(config)
                .region(aliyunOSSProperties.getRegion())
                .build();

        try {
            ossClient.putObject(aliyunOSSProperties.getBucketName(), objectName,
                    new ByteArrayInputStream(content));
        } finally {
            ossClient.shutdown();
        }

        // ❹ 返回公网 URL
        return "https://" + aliyunOSSProperties.getBucketName() + "."
                + aliyunOSSProperties.getEndpoint().replace("https://", "")
                + "/" + objectName;
    }
}
