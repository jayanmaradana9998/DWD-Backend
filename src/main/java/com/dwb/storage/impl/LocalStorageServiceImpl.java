package com.dwb.storage.impl;

import com.dwb.storage.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements StorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    // Runs once when the app starts — creates the uploads folder if it doesn't exist
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    @Override
    public String store(MultipartFile file, String subFolder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            // UUID ensures no two files ever have the same name, even if uploaded at the same time
            String uniqueFileName = UUID.randomUUID() + extension;

            Path targetFolder = Paths.get(uploadDir, subFolder);
            Files.createDirectories(targetFolder);

            Path targetPath = targetFolder.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetPath);

            // Return relative path so it can be stored in the DB
            return subFolder + "/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }
}
