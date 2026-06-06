package com.dwb.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/admin/files")
@RequiredArgsConstructor
@Tag(name = "Admin - KYC Management", description = "Admin endpoints for reviewing and actioning KYC submissions")
public class FileServeController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    // Streams the uploaded file back so admin can view it in Swagger or browser.
    // Example: GET /api/v1/admin/files/kyc/5/abc123.pdf
    // The {filePath} captures everything after /files/ including slashes.
    @GetMapping("/**")
    @Operation(summary = "View uploaded KYC document", description = "Serve an uploaded file by its stored path. Get the path from the KYC detail response.")
    public ResponseEntity<Resource> serveFile(
            @RequestAttribute(value = "javax.servlet.forward.request_uri", required = false) String uri,
            jakarta.servlet.http.HttpServletRequest request
    ) throws MalformedURLException {

        // Extract the relative file path from the URL after /api/v1/admin/files/
        String requestPath = request.getRequestURI();
        String prefix = "/api/v1/admin/files/";
        String relativePath = requestPath.substring(requestPath.indexOf(prefix) + prefix.length());

        Path filePath = Paths.get(uploadDir).resolve(relativePath).normalize().toAbsolutePath();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // Detect content type based on file extension for proper browser rendering
        String filename = filePath.getFileName().toString().toLowerCase();
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (filename.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
