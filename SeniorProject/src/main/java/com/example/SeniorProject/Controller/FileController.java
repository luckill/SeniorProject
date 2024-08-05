package com.example.SeniorProject.Controller;

import com.example.SeniorProject.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController
{
    @Autowired
    private S3Service s3Service;

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam()MultipartFile file)
    {
            String key = s3Service.uploadFile(file); ;
            return ResponseEntity.ok().body(s3Service.uploadFile(file));
    }
}