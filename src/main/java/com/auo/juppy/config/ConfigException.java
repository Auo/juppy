package com.auo.juppy.config;

public class ConfigException extends RuntimeException {
    public ConfigException(String message, Exception inner) {
        super(message, inner);
    }
}
