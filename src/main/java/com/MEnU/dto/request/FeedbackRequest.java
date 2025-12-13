package com.MEnU.dto.request;

public class FeedbackRequest {
    private String message;

    public FeedbackRequest() {}

    public FeedbackRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {}
}