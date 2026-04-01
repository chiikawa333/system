
package com.intelligent.driver.dto;

import lombok.Data;

@Data
public class AIChatResponse {
    private String content;
    private String conversationId;
    private Integer status;
    private String errorMessage;

    public static AIChatResponse success(String content, String conversationId) {
        AIChatResponse response = new AIChatResponse();
        response.setContent(content);
        response.setConversationId(conversationId);
        response.setStatus(200);
        return response;
    }

    public static AIChatResponse error(String errorMessage) {
        AIChatResponse response = new AIChatResponse();
        response.setErrorMessage(errorMessage);
        response.setStatus(500);
        return response;
    }
}
