package com.example.backend.file.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    public Path createDirectory(String dir) {
        Path directory = Paths.get(dir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException("디렉토리를 생성할 수 없습니다: " + dir, e);
        }
        return directory;
    }

    public String storeFile(byte[] fileData, String fileName, Path storageLocation) {
        try {
            // 고유 파일 이름 생성
            String uniqueFileName = generateUniqueFileName(fileName);
            Path targetLocation = storageLocation.resolve(uniqueFileName);
            Files.write(targetLocation, fileData);
            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + fileName, e);
        }
    }

    public void deleteFile(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생: " + fileName, e);
        }
    }

    public byte[] readFile(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 중 오류 발생: " + fileName, e);
        }
    }

    public void deleteDocumentFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생: " + filePath, e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String onlyFileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String uniqueFileName = onlyFileName + UUID.randomUUID().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        return uniqueFileName + extension;
    }
}
