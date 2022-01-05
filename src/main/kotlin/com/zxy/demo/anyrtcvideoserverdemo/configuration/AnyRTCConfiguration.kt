package com.zxy.demo.anyrtcvideoserverdemo.configuration

import com.zxy.demo.anyrtcvideoserverdemo.utils.anyrtc.AnyRTCClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AnyRTCConfiguration {
    @Bean
    fun anyRTCClient(anyRTCProperties: AnyRTCProperties):AnyRTCClient{
        val authorization = anyRTCProperties.authorization
        return AnyRTCClient(authorization.customerId,authorization.customerCertificate,authorization.appId)
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "anyrtc")
class AnyRTCProperties(
    val authorization:Authorization,
    val oss: OSS? = null
){
    @ConstructorBinding
    class Authorization(
        val customerId:String,
        val customerCertificate:String,
        val appId:String
    )

    @ConstructorBinding
    class OSS(
        val aliyun: Aliyun? = null
    ){
        @ConstructorBinding
        class Aliyun(
            val endpoint:String,
            val bucket:String,
            val accessKeyId:String,
            val accessKeySecret:String
        )
    }
}