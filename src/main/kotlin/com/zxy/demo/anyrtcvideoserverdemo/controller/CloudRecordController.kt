package com.zxy.demo.anyrtcvideoserverdemo.controller

import com.zxy.demo.anyrtcvideoserverdemo.configuration.AnyRTCProperties
import com.zxy.demo.anyrtcvideoserverdemo.utils.anyrtc.AnyRTCClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("cloudRecord")
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
        val uid = UUID.randomUUID().toString()
        val aliyun = this.anyRTCProperties.oss!!.aliyun!!
        val resourceId = anyRTCClient.acquire(startRecordRequest.cname, uid)
        anyRTCClient.startRecord(
            resourceId, AnyRTCClient.RecordMode.individual, AnyRTCClient.StartRecordRequest(
                startRecordRequest.cname, uid,
                AnyRTCClient.StartRecordClientRequest(
                    AnyRTCClient.StorageConfig(
                        AnyRTCClient.StorageVendor.aliyun,
                        AnyRTCClient.AliyunRegion.AP_Southeast_3,
                        aliyun.bucket,
                        aliyun.accessKeyId,
                        aliyun.accessKeySecret
                    ),
                    AnyRTCClient.RecordingConfig(
                        subscribeAudioUids = arrayListOf(startRecordRequest.uid),
                        subscribeVideoUids = arrayListOf(startRecordRequest.uid),
                        subscribeUidGroup = 0
                    )
                )
            )
        )
    }


}