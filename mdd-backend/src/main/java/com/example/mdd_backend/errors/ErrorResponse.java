package com.example.mdd_backend.errors;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse {

    private String message;
    private int status;
    private Map<String, String> fieldErrors;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ErrorResponse(String message, int status, Map<String, String> fieldErrors) {
        this.message = message;
        this.status = status;
        this.fieldErrors = fieldErrors;
    }
}
