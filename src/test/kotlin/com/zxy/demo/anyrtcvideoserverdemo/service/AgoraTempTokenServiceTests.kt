package com.zxy.demo.anyrtcvideoserverdemo.service

import com.zxy.demo.anyrtcvideoserverdemo.AnyrtcVideoServerDemoApplicationTests
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils

class AgoraTempTokenServiceTests : AnyrtcVideoServerDemoApplicationTests() {
    @Autowired
    private lateinit var agoraTempTokenService: AgoraTempTokenService
    @Test
    @Throws(Exception::class)
    fun testAgoraTempToken() {
        val agoraTempToken = agoraTempTokenService.getAgoraTempToken("zxy")
        println(agoraTempToken)
        Assertions.assertTrue(StringUtils.hasText(agoraTempToken))
    }
}