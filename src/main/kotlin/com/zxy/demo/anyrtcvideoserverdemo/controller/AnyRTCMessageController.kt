package com.zxy.demo.anyrtcvideoserverdemo.controller

import com.aliyun.oss.OSS
import com.zxy.demo.anyrtcvideoserverdemo.configuration.AliyunOSSProperties
import com.zxy.demo.anyrtcvideoserverdemo.service.MinIOService
import com.zxy.demo.anyrtcvideoserverdemo.utils.LoggerDelegate
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("anyrtc/events")
class AnyRTCMessageController(
    private val aliyunOSSClient: OSS,
    private val aliyunOSSProperties: AliyunOSSProperties,
    private val minIOService: MinIOService
) {

    companion object{
        private val log by LoggerDelegate()
    }

    @ExceptionHandler(Exception::class)
    fun anyException(e:Exception){
        log.error("Exception from anyrtc/events",e)
    }

    @PostMapping
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