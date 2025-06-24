package com.makers.project3.Service;

import com.makers.project3.exception.NoSuchEntityExistsException;
import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
public class UserService {

    @Autowired
    AuthenticatedUserService authenticatedUserService;
    @Autowired
    UserRepository userRepository;


//          Get the current User's DB Long id from their Auth0 id
    public  Long getCurrentUserId() {
        String userAuthId = authenticatedUserService.getAuthenticatedAuthId();
        User currentUser = userRepository.findUserByAuth0id(userAuthId).orElse(null);
        if (currentUser != null) {
            return currentUser.getId();
        } else {
            throw new NoSuchEntityExistsException("User");
        }
    }




// image upload -- WiP

//    public ResponseEntity<String> saveNewProfilePic (String fileName, MultipartFile imageFile, Long currentUser) throws IOException {
//        Path uploadPath = Paths.get("uploads/profilePics");
//        try {
//            String filePath = saveImage(imageFile);
//            return ResponseEntity.ok("Image uploaded successfully!");
//        } catch (IOException exception) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading Image");
//        }
//    }
//
//    private String saveImage(MultipartFile imageFile) throws IOException {
//        Path uploadPath = Paths.get("uploads/profilePics");
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//        if (imageFile.isEmpty()) {
//            throw new NoSuchFileException(imageFile.getOriginalFilename());
//        } else if {!imageFile.getContentType().equals("image/png") && !imageFile.getContentType().equals("image/jpg")) {
//            throw new IllegalArgumentException("Please upload a png or jpg image.");
//        }
//
//        String fileName = Long.toString(currentUser) + imageFile.getOriginalFilename().substring(-4);
//        Path filePath = uploadPath.resolve(fileName);
//    }
//
//    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
//        Path uploadPath = Paths.get(uploadDir);
//
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            Path filePath = uploadPath.resolve(fileName);
//            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException ioe) {
//            throw new IOException("Could not save image file: " + fileName, ioe);
//        }
//    }

}