package com.zxy.demo.anyrtcvideoserverdemo.controller

import com.zxy.demo.anyrtcvideoserverdemo.configuration.AnyRTCProperties
import com.zxy.demo.anyrtcvideoserverdemo.utils.anyrtc.AnyRTCClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("cloudRecord")
@RequestMapping
class CloudRecordController(
    private val anyRTCClient: AnyRTCClient,
    private val anyRTCProperties: AnyRTCProperties
) {

    class StartRecordRequest(
        val cname: String,
        val uid: String
    )

    @PostMapping("start")
    fun startRecord(@RequestBody startRecordRequest: StartRecordRequest) {
        val aliyun = this.anyRTCProperties.oss!!.aliyun!!
        val resourceId = anyRTCClient.acquire(startRecordRequest.cname, startRecordRequest.uid)
        anyRTCClient.startRecord(
            resourceId, AnyRTCClient.RecordMode.individual, AnyRTCClient.StartRecordRequest(
                AnyRTCClient.StorageConfig(
                    AnyRTCClient.StorageVendor.aliyun,
                    AnyRTCClient.AliyunRegion.AP_Southeast_3,
                    aliyun.bucket,
                    aliyun.accessKeyId,
                    aliyun.accessKeySecret
                )
            )
        )
    }


}