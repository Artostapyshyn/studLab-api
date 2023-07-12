package com.artostapyshyn.studlabapi.exception.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){
        super(message);
    }
}
