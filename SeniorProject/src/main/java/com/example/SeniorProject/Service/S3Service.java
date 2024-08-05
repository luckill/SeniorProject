package com.example.SeniorProject.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class S3Service
{
    private final S3Client s3Client;

    public S3Service(@Value("${aws.accessKeyId}") String accessKeyId, @Value("${aws.secretKey}") String secretKey, CompressedImageService compressedImageService)
    {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.US_WEST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    public String uploadFile(MultipartFile file)
    {
        String bucketName = "cdn12345";
        String keyName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        try
        {
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return keyName;
    }
}