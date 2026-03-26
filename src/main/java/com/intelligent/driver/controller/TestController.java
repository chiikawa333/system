package com.intelligent.driver.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.intelligent.driver.entity.Admin;
import com.intelligent.driver.util.Captcha;
import com.intelligent.driver.util.CaptchCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "调试接口")
@RestController
public class TestController {

    @Resource
    private CaptchCache captchCache;

    @Resource
    private DefaultKaptcha defaultKaptcha;

    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        return "Hello " + name;
    }

    @Operation(summary = "【调试】获取登录表单字段说明")
    @GetMapping("/debug/login-form")
    public Map<String, Object> loginForm() {
        Map<String, Object> form = new HashMap<>();
        form.put("说明", "请先调用 /common/getCaptcha 获取 captchaId 和图片");
        form.put("示例请求体", new Admin());
        form.put("captchaId", "从 /common/getCaptcha 接口获取");
        form.put("captchaCode", "用户输入的4位数字验证码");
        form.put("username", "已注册的用户名");
        form.put("userpwd", "密码明文（演示用，生产应加密）");
        return form;
    }

    @Operation(summary = "【调试】获取验证码（含真实图像）")
    @GetMapping("/debug/captcha")
    public Captcha getCaptcha() {
        Captcha captcha = new Captcha();
        String code = defaultKaptcha.createText(); // 真实生成

        // 使用 StpUtil.getTokenValue() 获取当前会话的token值
        // 如果没有token，则生成一个新的UUID作为ID
        String id = StpUtil.isLogin() ? StpUtil.getTokenValue() : java.util.UUID.randomUUID().toString();

        captchCache.storeCaptcha(id, code);

        BufferedImage image = defaultKaptcha.createImage(code);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            String base64Str = Base64.getEncoder().encodeToString(baos.toByteArray());
            captcha.setCaptchaImage("data:image/png;base64," + base64Str);
        } catch (Exception e) {
            captcha.setCaptchaImage("验证码图像生成失败");
        }

        captcha.setCaptchaId(id);
        return captcha;
    }



    @Operation(summary = "【调试】检查当前 Token 是否有效")
    @GetMapping("/debug/check-token")
    public Map<String, Object> checkToken() {
        Map<String, Object> result = new HashMap<>();
        boolean isLogin = StpUtil.isLogin();
        result.put("isLogin", isLogin);
        if (isLogin) {
            result.put("loginId", StpUtil.getLoginIdAsString());
            result.put("tokenName", StpUtil.getTokenName());
            result.put("tokenValue", StpUtil.getTokenValue());
        } else {
            result.put("message", "未登录，请携带 pdmtoken 请求");
        }
        return result;
    }

    @Operation(summary = "【调试】列出所有调试接口")
    @GetMapping("/debug/list-urls")
    public Map<String, String> listUrls() {
        Map<String, String> urls = new HashMap<>();
        urls.put("/debug/login-form", "登录表单说明");
        urls.put("/debug/captcha", "获取模拟验证码");
        urls.put("/debug/check-token", "检查 Token 状态");
        urls.put("/debug/admin-info", "获取当前管理员信息（需登录）");
        urls.put("/sayHello?name=xxx", "测试问候接口");
        return urls;
    }

    @Operation(summary = "【调试】获取当前管理员信息", description = "需要在 Header 中携带 pdmtoken")
    @GetMapping("/debug/admin-info")
    @SecurityRequirement(name = "pdmtoken")
    public Admin getAdminInfo() {
        if (!StpUtil.isLogin()) {
            throw new RuntimeException("未登录");
        }
        Admin admin = new Admin();
        admin.setId(StpUtil.getLoginIdAsLong());
        admin.setUsername("当前登录用户");
        admin.setName("测试姓名");
        admin.setTel("13800138000");
        return admin;
    }
}
