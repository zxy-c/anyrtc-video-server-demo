package com.zxy.demo.anyrtcvideoserverdemo.controller;

import com.zxy.demo.anyrtcvideoserverdemo.service.AgoraTempTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("agora/tempTokens")
@RestController
public class AgoraTempTokenController {

    private final AgoraTempTokenService agoraTempTokenService;

    public AgoraTempTokenController(
            AgoraTempTokenService agoraTempTokenService) {
        this.agoraTempTokenService = agoraTempTokenService;
    }

    @GetMapping
    public String getAgoraTempToken(@RequestParam String uid) throws Exception {
        return agoraTempTokenService.getAgoraTempToken(uid);
    }

}
