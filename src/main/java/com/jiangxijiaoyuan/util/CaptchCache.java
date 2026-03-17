package com.jiangxijiaoyuan.util;


import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class CaptchCache {
    private static ConcurrentHashMap<String,String> captchaMap = new ConcurrentHashMap<>();

    public void storeCaptcha(String captchaId,String captcha){
        captchaMap.put(captchaId,captcha);
    }

    public void removeCaptcha(String captchaId){
        captchaMap.remove(captchaId);
    }

    public Boolean validateCaptcha(String captchaId,String captcha){
        String captchaCode = captchaMap.get(captchaId);
        if (captchaCode == null){
            return false;
        }else {
            if (captchaCode.equals(captcha)){
                captchaMap.remove(captchaId);
                return true;
            }else {
                return false;
            }
        }
    }
}
