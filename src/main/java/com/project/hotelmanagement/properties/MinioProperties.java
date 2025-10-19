package com.project.hotelmanagement.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(
        prefix = "integration.minio"
        ,ignoreUnknownFields = false
)
public class MinioProperties {
    String minioAccessKey;
    String minioSecretKey;
    String minioUrl;
}
