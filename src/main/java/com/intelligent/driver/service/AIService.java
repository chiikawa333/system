
package com.intelligent.driver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.driver.dto.AIChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class AIService {

    @Value("${ai.siliconflow.api-key}")
    private String siliconflowApiKey;

    @Value("${ai.siliconflow.base-url}")
    private String siliconflowBaseUrl;

    @Value("${ai.siliconflow.model}")
    private String siliconflowModel;

    @Value("${ai.siliconflow.max-tokens}")
    private int siliconflowMaxTokens;

    @Value("${ai.siliconflow.temperature}")
    private double siliconflowTemperature;

    @Value("${ai.deepseek.api-key}")
    private String deepseekApiKey;

    @Value("${ai.deepseek.base-url}")
    private String deepseekBaseUrl;

    @Value("${ai.deepseek.model}")
    private String deepseekModel;

    @Value("${ai.deepseek.max-tokens}")
    private int deepseekMaxTokens;

    @Value("${ai.deepseek.temperature}")
    private double deepseekTemperature;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public AIChatResponse chat(String message) {
        return chat(message, "siliconflow");
    }

    public AIChatResponse chat(String message, String provider) {
        try {
            String apiKey = "deepseek".equals(provider) ? deepseekApiKey : siliconflowApiKey;
            String baseUrl = "deepseek".equals(provider) ? deepseekBaseUrl : siliconflowBaseUrl;
            String model = "deepseek".equals(provider) ? deepseekModel : siliconflowModel;
            int maxTokens = "deepseek".equals(provider) ? deepseekMaxTokens : siliconflowMaxTokens;
            double temperature = "deepseek".equals(provider) ? deepseekTemperature : siliconflowTemperature;

            String requestBody = buildRequestBody(message, model, maxTokens, temperature);

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

    public SseEmitter chatStream(String message) {
        return chatStream(message, "deepseek");
    }

    public SseEmitter chatStream(String message, String provider) {
        SseEmitter emitter = new SseEmitter(60000L);
        
        CompletableFuture.runAsync(() -> {
            try {
                String apiKey = "deepseek".equals(provider) ? deepseekApiKey : siliconflowApiKey;
                String baseUrl = "deepseek".equals(provider) ? deepseekBaseUrl : siliconflowBaseUrl;
                String model = "deepseek".equals(provider) ? deepseekModel : siliconflowModel;
                int maxTokens = "deepseek".equals(provider) ? deepseekMaxTokens : siliconflowMaxTokens;
                double temperature = "deepseek".equals(provider) ? deepseekTemperature : siliconflowTemperature;

                String requestBody = buildRequestBody(message, model, maxTokens, temperature, true);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/v1/chat/completions"))
                        .timeout(Duration.ofSeconds(60))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Accept", "text/event-stream")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                httpClient.send(request, HttpResponse.BodyHandlers.ofLines()).body().forEach(line -> {
                    try {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                emitter.complete();
                            } else {
                                JsonNode jsonNode = objectMapper.readTree(data);
                                String content = jsonNode.path("choices").path(0).path("delta").path("content").asText();
                                if (!content.isEmpty()) {
                                    emitter.send(SseEmitter.event().data(content));
                                }
                            }
                        }
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                });

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private String buildRequestBody(String message, String model, int maxTokens, double temperature) {
        return buildRequestBody(message, model, maxTokens, temperature, false);
    }

    private String buildRequestBody(String message, String model, int maxTokens, double temperature, boolean stream) {
        String systemPrompt = "deepseek".equals(model.split("/")[0]) || model.contains("v4") 
            ? "你是一个云端管理助手，精通系统运维、数据分析、业务监控等管理工作。" +
              "你能提供运营建议、数据解读、异常预警等专业指导。" +
              "回答简洁清晰，逻辑严谨，语气专业且高效。"
            : "你是一个专业的物流助手，精通仓储管理、运输调度、库存优化等物流业务。" +
              "你能提供入库出库建议、路径规划、运力调配等专业指导。" +
              "回答简洁实用，数据准确，语气专业但不失亲切。";
        
        if (stream) {
            return String.format(
                    "{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":\"%s\"},{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":%d,\"temperature\":%.1f,\"stream\":true}",
                    model,
                    escapeJson(systemPrompt),
                    escapeJson(message),
                    maxTokens,
                    temperature
            );
        } else {
            return String.format(
                    "{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":\"%s\"},{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":%d,\"temperature\":%.1f}",
                    model,
                    escapeJson(systemPrompt),
                    escapeJson(message),
                    maxTokens,
                    temperature
            );
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
