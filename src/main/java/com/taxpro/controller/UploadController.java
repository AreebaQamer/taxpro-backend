package com.taxpro.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://sqamer.com", "https://www.sqamer.com"}, allowCredentials = "true")
public class UploadController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "File empty hai");
            return ResponseEntity.badRequest().body(response);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            response.put("error", "Sirf image files allowed hain");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "sqamer-blogs")
            );

            String fileUrl = (String) uploadResult.get("secure_url");
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Upload fail: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}