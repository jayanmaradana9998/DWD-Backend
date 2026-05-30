package com.dwb.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    // Saves a file and returns the path where it was stored
    String store(MultipartFile file, String subFolder);

    // Deletes a file by its stored path
    void delete(String filePath);
}
