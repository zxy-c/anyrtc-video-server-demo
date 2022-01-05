package com.zxy.demo.anyrtcvideoserverdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class AnyrtcVideoServerDemoApplication

fun main(args: Array<String>) {
    runApplication<AnyrtcVideoServerDemoApplication>(*args)
}
