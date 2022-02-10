package com.zxy.demo.anyrtcvideoserverdemo.service;

import com.zxy.demo.anyrtcvideoserverdemo.configuration.AgoraProperties;
import io.agora.media.AccessToken;
import io.agora.rtm.RtmTokenBuilder;
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

    public String getAgoraRtcToken(String channelName) throws Exception {
        AccessToken token = new AccessToken(
                agoraProperties.getAppId(), agoraProperties.getAppCertificate(),
                channelName,
                "" // don't need to validate any user
        );
        int privilegeTs = (int)(System.currentTimeMillis() / 1000 + (60*60*24));
        token.addPrivilege(AccessToken.Privileges.kJoinChannel, privilegeTs);
        token.addPrivilege(AccessToken.Privileges.kPublishAudioStream, privilegeTs);
        token.addPrivilege(AccessToken.Privileges.kPublishVideoStream, privilegeTs);
        token.addPrivilege(AccessToken.Privileges.kPublishDataStream, privilegeTs);


        return token.build();

    }
}
