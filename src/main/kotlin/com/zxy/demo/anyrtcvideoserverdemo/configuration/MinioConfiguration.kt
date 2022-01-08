package com.zxy.demo.anyrtcvideoserverdemo.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "minio")
@ConstructorBinding
class MinioConfiguration(
    val endpoint: String,
    val secretKey: String,
    val accessKey: String,
    val bucketName: String
)