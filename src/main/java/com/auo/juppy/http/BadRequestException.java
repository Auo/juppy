package com.auo.juppy.http;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
