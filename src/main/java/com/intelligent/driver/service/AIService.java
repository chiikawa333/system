
package com.intelligent.driver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.driver.dto.AIChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class AIService {

    @Value("${ai.siliconflow.api-key}")
    private String apiKey;

    @Value("${ai.siliconflow.base-url}")
    private String baseUrl;

    @Value("${ai.siliconflow.model}")
    private String model;

    @Value("${ai.siliconflow.max-tokens}")
    private int maxTokens;

    @Value("${ai.siliconflow.temperature}")
    private double temperature;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public AIChatResponse chat(String message) {
        try {
            String requestBody = buildRequestBody(message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/chat/completions"))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                String content = jsonNode.path("choices").path(0).path("message").path("content").asText();
                String conversationId = jsonNode.path("id").asText();
                return AIChatResponse.success(content, conversationId);
            } else {
                System.err.println("=== AI API 请求失败，状态码：" + response.statusCode() + " ===");
                System.err.println("响应内容：" + response.body());
                return AIChatResponse.error("AI API 请求失败，状态码：" + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AIChatResponse.error("AI 服务调用失败：" + e.getMessage());
        }
    }

    private String buildRequestBody(String message) {
        // 添加系统提示词，让 AI 扮演更生动的角色
        String systemPrompt = "你是一个智能车载助手，名字叫'小驰'。你性格活泼、幽默、贴心，说话自然流畅，就像用户的朋友。" +
                "你不会说'作为 AI'、'我无法'这类话，而是积极提供帮助。" +
                "回答简洁实用，偶尔开个玩笑，但不过分。" +
                "你了解汽车知识，能给出专业的用车建议。";
        
        return String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":\"%s\"},{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":%d,\"temperature\":%.1f}",
                model,
                escapeJson(systemPrompt),
                escapeJson(message),
                maxTokens,
                temperature
        );
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
