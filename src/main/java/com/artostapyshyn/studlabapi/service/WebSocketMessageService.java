package com.artostapyshyn.studlabapi.service;
import java.util.List;

public interface WebSocketMessageService {
    <T> void sendPayloads(List<T> payloads, String destination);
}
