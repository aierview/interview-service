package com.aierview.backend.interview.infra.adapter.bucket;

import com.aierview.backend.interview.domain.contract.bucket.IUploadBase64File;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2BucketAdapter implements IUploadBase64File {
    private final S3Client r2Client;

    @Value("${r2.audio_file_path}")
    private String audioFilePath;

    @Value("${r2.bucket}")
    private String bucket;


    @Override
    public String execute(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        String filename = audioFilePath + "/" + UUID.randomUUID();
        r2Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(filename)
                        .contentType("audio/webm")
                        .build(),
                RequestBody.fromBytes(bytes)
        );
        return filename;
    }
}
