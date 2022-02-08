package com.zxy.demo.anyrtcvideoserverdemo.service;

import com.zxy.demo.anyrtcvideoserverdemo.configuration.AgoraProperties;
import io.agora.rtm.RtmTokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AgoraTempTokenService {

    private final AgoraProperties agoraProperties;

    public AgoraTempTokenService(AgoraProperties agoraProperties) {
        this.agoraProperties = agoraProperties;
    }

    public String getAgoraTempToken(String uid) throws Exception {
        return new RtmTokenBuilder().buildToken(agoraProperties.getAppId(),agoraProperties.getAppCertificate(),
                uid, RtmTokenBuilder.Role.Rtm_User, Math.toIntExact(Duration.ofDays(1).toMillis()));
    }
}
