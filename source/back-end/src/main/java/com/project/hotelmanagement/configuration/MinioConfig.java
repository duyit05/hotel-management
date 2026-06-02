package com.project.hotelmanagement.configuration;

import com.project.hotelmanagement.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient () {
        return MinioClient.builder()
                .endpoint(minioProperties.getMinioUrl())
                .credentials(minioProperties.getMinioAccessKey(), minioProperties.getMinioSecretKey())
                .build();
    }
}
