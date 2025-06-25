package com.studyhub.study_chat.common.exception;

public class UnreachableError extends ClientError {
    public UnreachableError(String message) {
        this.errorCode = "UnreachableError";
        this.errorMessage = message;
    }
}
