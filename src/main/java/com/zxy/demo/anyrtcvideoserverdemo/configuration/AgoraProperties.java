package com.zxy.demo.anyrtcvideoserverdemo.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@AllArgsConstructor
@Getter
@ConfigurationProperties(prefix = "agora")
@ConstructorBinding
public class AgoraProperties {

    private final String appId;

    private final String appCertificate;

}
