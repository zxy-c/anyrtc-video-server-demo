package com.zxy.demo.anyrtcvideoserverdemo.configuration

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AliyunOSSConfiguration {
    @Bean
    fun aliyunOSSClient(aliyunOssProperties: AliyunOSSProperties): OSS {
        return OSSClientBuilder().build(
            aliyunOssProperties.endpoint,
            aliyunOssProperties.accessKeyId,
            aliyunOssProperties.accessKeySecret
        )
    }

}

@ConstructorBinding
@ConfigurationProperties(prefix = "aliyun.oss")
class AliyunOSSProperties(
    val endpoint: String,
    val bucket: String,
    val accessKeyId: String,
    val accessKeySecret: String
)