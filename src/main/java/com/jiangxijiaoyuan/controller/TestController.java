package com.jiangxijiaoyuan.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name){
        return "Hello" + name;
    }
}
