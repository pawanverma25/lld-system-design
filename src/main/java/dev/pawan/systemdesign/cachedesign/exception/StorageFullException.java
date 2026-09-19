package dev.pawan.systemdesign.cachedesign.exception;

public class StorageFullException extends RuntimeException{
    public StorageFullException(String message){
        super(message);
    }
}
