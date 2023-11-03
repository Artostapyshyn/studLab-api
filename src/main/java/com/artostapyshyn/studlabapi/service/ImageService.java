package com.artostapyshyn.studlabapi.service;

import java.io.IOException;

public interface ImageService {
    byte[] compressImage(byte[] imageBytes) throws IOException;
}
