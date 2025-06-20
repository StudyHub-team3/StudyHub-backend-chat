package com.studyhub.study_chat.common.exception;

public class NotFound extends ClientError {
    public NotFound(String message) {
        this.errorCode = "Not Found";
        this.errorMessage = message;
    }
}
