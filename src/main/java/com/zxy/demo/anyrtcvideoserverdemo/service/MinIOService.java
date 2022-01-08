package com.zxy.demo.anyrtcvideoserverdemo.service;

import com.zxy.demo.anyrtcvideoserverdemo.configuration.MinioConfiguration;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class MinIOService {

    public static final String NO_SUCH_KEY = "NoSuchKey";
    private final MinioConfiguration configuration;

    private final MinioClient minioClient;

    @Autowired
    public MinIOService(MinioConfiguration minioConfiguration) {
        this.configuration = minioConfiguration;
        this.minioClient = createMinoClient(false);
    }


    private MinioClient createMinoClient(boolean enabledSSL) {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(configuration.getEndpoint())
                        .credentials(configuration.getAccessKey(), configuration.getSecretKey())
                        .build();

        return minioClient;
    }

    public ObjectWriteResponse uploadFile(InputStream inputStream, long fileSize, String fileName, String contentType) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        log.info("Start push object {} to MinIO", fileName);
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(configuration.getBucketName())
                .stream(inputStream, fileSize, -1)
                .object(fileName)
                .contentType(contentType)
                .build();

        log.info("Start pushing the data with file name {}", fileName);
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(putObjectArgs);
        log.info("Upload file to minIO finished");
        return objectWriteResponse;
    }

    public String generateUploadPresignedUrl(String pathFile, Method method) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        String url =
                minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(method)
                                .bucket(configuration.getBucketName())
                                .object(pathFile)
                                .expiry(60 * 60 * 24)
                                .build());
        log.info("path of url {}", url);
        return url;
    }

    public byte[] getFile(String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (!objectExist(filePath)) {
            throw new FileNotFoundException(String.format("File %s does not exist in bucket %s", filePath, configuration.getBucketName()));
        }
        GetObjectResponse getObjectResponse = minioClient.getObject(GetObjectArgs.builder()
                .bucket(configuration.getBucketName())
                .object(filePath)
                .build());
        return IOUtils.toByteArray(getObjectResponse);
    }

    public boolean objectExist(String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(configuration.getBucketName())
                    .object(filePath)
                    .build());
            return true;
        } catch (ErrorResponseException exception) {
            if (NO_SUCH_KEY.equals(exception.errorResponse().code())) {
                return false;
            }
            return true;
        } catch (Exception exception) {
            log.error("Error while downloading file from MinIO", exception);
            return false;
        }
    }

}
