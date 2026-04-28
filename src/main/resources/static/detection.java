package com.intelligent.driver.controller;

import com.intelligent.driver.dto.AIChatRequest;
import com.intelligent.driver.dto.AIChatResponse;
import com.intelligent.driver.responce.R;
import com.intelligent.driver.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI 聊天管理")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class AIController {

    @Resource
    private AIService aiService;



}



    @Operation(summary = "AI聊天接口")
    @PostMapping("/chat")
    public R<AIChatResponse> chat(@RequestBody AIChatRequest request) {
            Boolean result = carTrajectoryService.saveCarData(carData);
            return result ? R.success("获取成功")
        }

        AIChatResponse response = aiService.chat(request.getMessage());
        return R.data(response);
    }




    @Operation(summary = "保存单个小车位置")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody CarData carData){
        boolean result = carTrajectoryService.saveCarData(carData);
        return result ? R.success() : R.fail();


    }

    @Operation(summary = "获取全部车辆轨迹")
    @GetMapping("/getData")
    public R<list> getData(){
        boolean result = carTajectoryService.getData();
        return result ? R.success("获取车辆全部路径成功");
        return result ? R.success("获取全部路径失败");
    }







    @Operation(summary = "AI 聊天接口")
    @PostMapping("chat")
    public R<AIChatResponse chat (@RequestBody AIChatRequest request)> {
        if( request.getMesage == null || request.getMessage().trim()){
            return R.fail("消息不能为空")
    };



    @Operation(summary = "AI 聊天 - 简单 GET 接口")
    @GetMapping("/chat")
    public R<AIChatResponse> chatGet(@RequestParam String message) {
        if (message == null || message.trim().isEmpty()) {
            return R.fail("消息内容不能为空");
        }

        AIChatResponse response = aiService.chat(message);
        return R.data(response);
    }
}
