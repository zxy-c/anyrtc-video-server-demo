package com.zxy.demo.anyrtcvideoserverdemo.controller

import com.aliyun.oss.OSS
import com.zxy.demo.anyrtcvideoserverdemo.configuration.AliyunOSSProperties
import com.zxy.demo.anyrtcvideoserverdemo.service.MinIOService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("anyrtc/events")
class AnyRTCMessageController(
    private val aliyunOSSClient: OSS,
    private val aliyunOSSProperties: AliyunOSSProperties,
    private val minIOService: MinIOService
) {

    @RequestMapping
    fun events() {
        // TODO calllback from
        val key = ""
        val ossObject = aliyunOSSClient.getObject(aliyunOSSProperties.bucket, key)
        val uid = ""
        minIOService.uploadFile(
            ossObject.objectContent,
            ossObject.objectMetadata.contentLength,
            "video/livestreaming/$uid/$key",
            ossObject.objectMetadata.contentType
        )
    }

}