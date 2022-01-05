package com.zxy.demo.anyrtcvideoserverdemo.utils.anyrtc

import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*

class AnyRTCClient(private val customerId: String, private val customerSecret: String, private val appId: String) {

    private val restTemplate = RestTemplate().apply {
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
            val newRequest = ReplaceUrlHttpRequest(request, appId)
            execution.execute(newRequest, body)
        })
    }

    private class ReplaceUrlHttpRequest(val httpRequest: HttpRequest, private val appId: String) :
        HttpRequest by httpRequest {
        override fun getURI(): URI {
            return UriComponentsBuilder.fromUri(httpRequest.uri).host("https://api.agrtc.cn")
                .replacePath("v1/apps/${appId}/" + httpRequest.uri.path.removePrefix("/"))
                .build()
                .toUri()
        }
    }

    fun acquire(cname:String,uid:String): String {
        return restTemplate.postForEntity<AnyRTCBasicResponse<AnyRTCAcquireResponse>>(
            "cloud_recording/acquire",
            mapOf("cname" to cname, "uid" to uid)
        ).body!!.Body.resourceId
    }

    data class AnyRTCAcquireResponse(
        val resourceId:String
    )

    @Suppress("EnumEntryName")
    enum class RecordMode{
        individual,mix
    }

    @Suppress("EnumEntryName")
    enum class StorageVendor(
        @field:JsonValue
        val id:Int
    ){
        aliyun(2)
    }

    interface StorageRegion {
        val number:Int
        @JsonValue
        fun number(): Int {
            return this.number
        }
    }

    enum class AliyunRegion(override val number: Int):StorageRegion{
        AP_Southeast_3(12)
    }

    data class StorageConfig(
        val vendor:StorageVendor,
        val region:AliyunRegion,
        val bucket:String,
        val accessKey:String,
        val secretKey:String,
        val fileNamePrefix:List<String> = emptyList()
    )

    data class StartRecordRequest(
        val storageConfig: StorageConfig? = null
    )

    fun startRecord(resourceId: String,mode:RecordMode,startRecordRequest: StartRecordRequest = StartRecordRequest()): ResponseEntity<StartRecordResponse> {
        return this.restTemplate.postForEntity("cloud_recording/resourceid/${resourceId}/mode/${mode.name}/start",startRecordRequest)
    }

    data class StartRecordResponse(
        val sid:String,
        val resourceId:String
    )

}