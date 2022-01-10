package com.zxy.demo.anyrtcvideoserverdemo.controller

import com.aliyun.oss.OSS
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.zxy.demo.anyrtcvideoserverdemo.configuration.AliyunOSSProperties
import com.zxy.demo.anyrtcvideoserverdemo.service.MinIOService
import com.zxy.demo.anyrtcvideoserverdemo.utils.LoggerDelegate
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("anyrtc/events")
class AnyRTCMessageController(
    private val aliyunOSSClient: OSS,
    private val aliyunOSSProperties: AliyunOSSProperties,
    private val minIOService: MinIOService,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private val log by LoggerDelegate()
    }

    @ExceptionHandler(Exception::class)
    fun anyException(e: Exception) {
        log.error("Exception from anyrtc/events", e)
    }

    data class AnyRTCEvent(
        val eventType: Int,
        val productId: Int,
        val noticeID: String,
        val notifyMs: Long,
        val payload: Payload
    ) {
        data class Payload(
            val cname: String,
            val uid: String,
            val sid: String,
            val sequence: Int,
            val sendts: Long,
            val serviceType: Int,
            val details: JsonNode
        ) {
            data class UploadedEvent(
                val msgName: String,
                val status: String,
                val fileList: List<FileItem>
            ) {
                data class FileItem(
                    val fileName: String,
                    val trackType: Type,
                    val uid: String,
                    val mixedAllUser: Boolean,
                    val isPlayable: Boolean,
                    val liceStartTime: Long
                ) {
                    @Suppress("EnumEntryName")
                    enum class Type {
                        audio_and_video,
                        audio,
                        video
                    }
                }
            }
        }
    }


    @PostMapping(consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE,MediaType.APPLICATION_JSON_VALUE])
    fun events(@RequestBody body: String) {
        log.info("anyrtc event : {}",body)
        val anyRTCEvent = objectMapper.readValue<AnyRTCEvent>(body)
        if (anyRTCEvent.eventType==31){
            val uploadedEvent = objectMapper.treeToValue<AnyRTCEvent.Payload.UploadedEvent>(anyRTCEvent.payload.details)
            log.info("uploaded event: {}",uploadedEvent)
//            // TODO calllback from
//            val key = ""
//            val ossObject = aliyunOSSClient.getObject(aliyunOSSProperties.bucket, key)
//            val uid = ""
//            minIOService.uploadFile(
//                ossObject.objectContent,
//                ossObject.objectMetadata.contentLength,
//                "video/livestreaming/$uid/$key",
//                ossObject.objectMetadata.contentType
//            )
        }
    }

}