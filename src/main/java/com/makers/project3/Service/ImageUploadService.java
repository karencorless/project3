package com.makers.project3.Service;

import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageUploadService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Value("${upload.profile}")
    private String uploadProfileDir;

    public String handleProfileImageUpload(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Empty image file");
        }

        User user = authenticatedUserService.getAuthenticatedUser();

        String extension = getFileExtension(image.getOriginalFilename());
        String sanitizedId = user.getAuth0id().replaceAll("[^a-zA-Z0-9_-]", "_");
        String profileFileName = sanitizedId + "." + extension;

        Path uploadPath = Paths.get(uploadProfileDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (user.getImageSource() != null && !user.getImageSource().isEmpty()) {
            String oldFileName = user.getImageSource().replace("/uploads/profilePics/", "");
            Path oldImagePath = uploadPath.resolve(oldFileName);
            if (!oldFileName.equals("default.jpg")) {
                try {
                    Files.deleteIfExists(oldImagePath);
                } catch (IOException e) {
                    System.err.println("Failed to delete old profile image: " + e.getMessage());
                }
            }
        }

        Path destination = uploadPath.resolve(profileFileName);
        image.transferTo(destination.toFile());

        user.setImageSource("/uploads/profilePics/" + profileFileName);
        userRepository.save(user);

        return "/uploads/profilePics/" + profileFileName;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex != -1) ? filename.substring(dotIndex + 1) : "";
    }
}
