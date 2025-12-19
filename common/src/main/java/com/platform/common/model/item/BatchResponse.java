package com.platform.common.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchResponse {

    @JsonProperty("success_count")
    private int successCount = 0;

    @JsonProperty("failure_count")
    private int failureCount = 0;

    private List<ErrorItem> errors = new ArrayList<>();

    public void incrementSuccess() {
        successCount++;
    }

    public void addError(String key, String msg) {
        failureCount++;
        errors.add(new ErrorItem(key, msg));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorItem {

        @JsonProperty("ref_key")
        private String refKey;

        private String message;
    }
}