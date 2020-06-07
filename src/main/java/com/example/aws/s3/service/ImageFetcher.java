package com.example.aws.s3.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class ImageFetcher {
    private static final Logger logger = LoggerFactory.getLogger(ImageFetcher.class);
    String accessKey = "";
    String secretKey = "";

    public List<String> getImage() {
        List<S3Object> s3Objects = getS3Objects();
        List<String> images = new ArrayList<>();
        for (S3Object s3Object : s3Objects) {
            BufferedImage imgBuf;
            try {
                imgBuf = ImageIO.read(s3Object.getObjectContent());
                logger.info(s3Object.getKey());
                images.add(encodeBase64URL(imgBuf));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    private AmazonS3 getS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }

    private List<S3Object> getS3Objects() {
        String bucketName = "";
        ObjectListing objectListing = getS3Client().listObjects(bucketName);
        List<S3Object> s3Objects = new ArrayList<>();
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            s3Objects.add(getS3Client().getObject(new GetObjectRequest(bucketName, os.getKey())));
        }
        return s3Objects;
    }

    private String encodeBase64URL(BufferedImage imgBuf) throws IOException {
        logger.info("ActionLog.encodeBase64URL.start");
        String base64;
        byte[] bytes;
        if (imgBuf == null) {
            base64 = null;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(imgBuf, "PNG", out);
            bytes = out.toByteArray();
            base64 = "data:image/png;base64," + new String(Base64.getEncoder().encode(bytes), "UTF-8");
        }
        logger.info("ActionLog.encodeBase64URL.end");
        return base64;
    }
}
