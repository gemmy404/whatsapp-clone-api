package com.whatsappclone.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
public class FileService {

    @Value("${application.file.uploads.media-output-path}")
    private String fileUploadPath;

    public String saveFile(@NonNull MultipartFile sourceFile, @NonNull String userId) {
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile, @NonNull String fileUploadSubPath) {
        final String finalFileUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalFileUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.error("Failed to create the target folder: {}", fileUploadPath);
                return null;
            }
        }
        String fileExtension = extractFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalFileUploadPath + separator + System.currentTimeMillis() + '.' + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try (OutputStream outputStream = Files.newOutputStream(targetPath)) {
            outputStream.write(sourceFile.getBytes());
            log.info("Successfully uploaded file: {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("File was not saved ", e);
        }
        return null;
    }

    private String extractFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    public static byte[] readFileFromLocation(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return new byte[0];
        }
        try {
            Path path = Paths.get(fileUrl);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("No file found in the path {}", fileUrl);
        }
        return new byte[0];
    }

}
