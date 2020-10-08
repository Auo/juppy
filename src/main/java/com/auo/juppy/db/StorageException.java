package com.auo.juppy.db;

public class StorageException extends RuntimeException {
    public StorageException(String message, Exception innerException) {
        super(message, innerException);
    }

    public StorageException(Exception innerException) {
        super(innerException);
    }

    public StorageException(String message) {
        super(message);
    }
}
