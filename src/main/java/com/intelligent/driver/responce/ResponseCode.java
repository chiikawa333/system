package com.intelligent.driver.responce;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200,"操作成功"),
    ERROR(500,"操作失败"),
    USERNAME_EXIST(1001,"用户名已存在！"),
    CAPTCHA_ERROR(2002,"验证码错误"),
    USERNAME_USERPED_ERROR(2003,"用户名密码错误"),

    CREATE_CAPTCHA_ERROR(2001,"创建验证码失败");

    private Integer code;
    private String message;
}
