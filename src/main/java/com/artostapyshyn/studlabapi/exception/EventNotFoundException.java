package com.artostapyshyn.studlabapi.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(String message){
        super(message);
    }
}
