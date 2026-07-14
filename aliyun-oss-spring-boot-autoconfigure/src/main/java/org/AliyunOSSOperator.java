package org.example.utils;

import com.aliyun.oss.*;
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

@Slf4j
@Component
public class AliyunOSSOperator {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    public String upload(byte[] content, String originalFilename) throws Exception {
        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();
        String accessKeyId = aliyunOSSProperties.getAccessKeyId();
        String accessKeySecret = aliyunOSSProperties.getAccessKeySecret();
        String region = aliyunOSSProperties.getRegion();
        DefaultCredentialProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String ext = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            ext = originalFilename.substring(dotIndex);
        }
        String newFileName = UUID.randomUUID() + ext;
        String objectName = dir + "/" + newFileName;

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
        } finally {
            ossClient.shutdown();
        }

        return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;
    }

}
