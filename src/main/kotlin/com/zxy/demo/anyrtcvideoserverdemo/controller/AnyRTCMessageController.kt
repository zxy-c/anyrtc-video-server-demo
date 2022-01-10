package com.zxy.demo.anyrtcvideoserverdemo.controller

import com.aliyun.oss.OSS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zxy.demo.anyrtcvideoserverdemo.configuration.AliyunOSSProperties
import com.zxy.demo.anyrtcvideoserverdemo.service.MinIOService
import com.zxy.demo.anyrtcvideoserverdemo.utils.LoggerDelegate
import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

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
            val details: String
        ) {
            data class UploadedEvent(
                val msgName: String,
                val status: String,
                val fileList: List<FileItem>
            ) {
                data class FileItem(
                    val filename: String,
                    val trackType: Type,
                    val uid: String,
                    val mixedAllUser: Boolean,
                    val isPlayable: Boolean,
                    val sliceStartTime: Long
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


    @PostMapping(consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE])
    fun events(@RequestBody body: String) {
        log.info("anyrtc event : {}", body)
        val anyRTCEvent = objectMapper.readValue<AnyRTCEvent>(body)
        if (anyRTCEvent.eventType == 31) {
            val uploadedEvent = objectMapper.readValue<AnyRTCEvent.Payload.UploadedEvent>(anyRTCEvent.payload.details)
            log.info("uploaded event: {}", uploadedEvent)

            uploadedEvent.fileList.find { it.trackType == AnyRTCEvent.Payload.UploadedEvent.FileItem.Type.audio_and_video }
                ?.let { m3u8File ->
                    val filename = m3u8File.filename
                    val ossObject = aliyunOSSClient.getObject(aliyunOSSProperties.bucket, filename)
                    val uid = m3u8File.uid
                    val m3u8FileContent = IOUtils.toString(ossObject.objectContent, Charset.defaultCharset())
                    log.info("uid:{} m3u8File:{}\n{}", uid, m3u8File, m3u8FileContent)

                    minIOService.uploadFile(
                        ByteArrayInputStream(m3u8FileContent.toByteArray()),
                        ossObject.objectMetadata.contentLength,
                        "video/livestreaming/$uid/$filename",
                        ossObject.objectMetadata.contentType
                    )

                    m3u8FileContent.lines().filter {
                        it.endsWith(".ts")
                    }.forEach { tsFileName ->
                        val tsObject = aliyunOSSClient.getObject(aliyunOSSProperties.bucket, tsFileName)
                        log.info("uid:{} ts:{}", uid,tsFileName)
                        minIOService.uploadFile(
                            tsObject.objectContent, tsObject.objectMetadata.contentLength,
                            "video/livestreaming/$uid/$tsFileName",
                            tsObject.objectMetadata.contentType
                        )
                    }

                }

        }
    }

}