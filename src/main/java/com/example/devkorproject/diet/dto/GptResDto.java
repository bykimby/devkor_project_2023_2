package com.example.devkorproject.diet.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Getter
@Setter
public class GptResDto {
    @JsonProperty
    private String id;
    @JsonProperty
    private String object;
    @JsonProperty
    private long created;
    @JsonProperty
    private String model;
    @JsonProperty
    private Usage usage;
    @JsonProperty
    private List<Choice> choices;

    @Getter
    @Setter
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;
    }

    @Getter
    @Setter
    public static class Choice {
        @JsonProperty
        private ChatGptMessage message;
        @JsonProperty("finish_reason")
        private String finishReason;
        @JsonProperty
        private int index;
    }
}