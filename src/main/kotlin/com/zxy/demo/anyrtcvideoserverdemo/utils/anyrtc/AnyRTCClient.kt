package com.zxy.demo.anyrtcvideoserverdemo.utils.anyrtc

import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.DefaultUriBuilderFactory
import java.util.*

class AnyRTCClient(private val customerId: String, private val customerSecret: String, private val appId: String) {

    private val restTemplate = RestTemplate().apply {
        this.uriTemplateHandler = DefaultUriBuilderFactory("https://api.agrtc.cn/v1/apps/${appId}/")
        val authorizationValue =
            "Basic " + Base64.getEncoder().encodeToString("$customerId:$customerSecret".toByteArray())
        this.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers.add(
                HttpHeaders.AUTHORIZATION,
                authorizationValue
            )
            execution.execute(request, body)
        })
        this.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
//            request.headers.contentType = MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
            // 竟然不接受大写UTF-8
            request.headers.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8")
            execution.execute(request, body)
        })
    }


    fun acquire(cname: String, uid: String): String {
        val httpEntity = HttpEntity<Any>(mapOf("cname" to cname, "uid" to uid))
        return restTemplate.exchange<AnyRTCBasicResponse<AnyRTCAcquireResponse>>(
            "cloud_recording/acquire",
            HttpMethod.POST,
            httpEntity
        ).body!!.Body.resourceId
    }

    data class AnyRTCAcquireResponse(
        val resourceId: String
    )

    @Suppress("EnumEntryName")
    enum class RecordMode {
        individual, mix
    }

    @Suppress("EnumEntryName")
    enum class StorageVendor(
        @field:JsonValue
        val id: Int
    ) {
        aliyun(2)
    }

    interface StorageRegion {
        val number: Int

        @JsonValue
        fun number(): Int {
            return this.number
        }
    }

    enum class AliyunRegion(override val number: Int) : StorageRegion {
        AP_Southeast_3(12)
    }

    data class StorageConfig(
        val vendor: StorageVendor,
        val region: AliyunRegion,
        val bucket: String,
        val accessKey: String,
        val secretKey: String,
        val fileNamePrefix: List<String> = emptyList()
    )

    data class StartRecordRequest(
        val cname:String,
        val uid: String,
        val clientRequest: StartRecordClientRequest
    )

    data class StartRecordClientRequest(
        val storageConfig: StorageConfig? = null,
        val recordingConfig:RecordingConfig= RecordingConfig()
    )

    enum class ChannelType(
        @field:JsonValue
        val number:Int
    ) {
        COMMUNICATE(0),
        LIVE(1)
    }

    enum class StreamType(
        @field:JsonValue
        val number:Int
    ){
        AUDIO(0),
        VIDEO(1),
        ALL(2)
    }


    data class RecordingConfig(
        val channelType:ChannelType = ChannelType.COMMUNICATE,
        val streamTypes:StreamType = StreamType.ALL,
        val subscribeVideoUids:List<String> = emptyList(),
        val subscribeAudioUids:List<String> = emptyList(),
        val subscribeUidGroup: Int? = null
    )

    fun startRecord(
        resourceId: String,
        mode: RecordMode,
        startRecordRequest: StartRecordRequest
    ): ResponseEntity<AnyRTCBasicResponse<StartRecordResponse>> {
        return this.restTemplate.exchange(
            "cloud_recording/resourceid/${resourceId}/mode/${mode.name}/start",
            HttpMethod.POST,
            HttpEntity(startRecordRequest)
        )
    }

    data class StartRecordResponse(
        val sid: String,
        val resourceId: String
    )

}