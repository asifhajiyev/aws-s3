package com.example.aws.s3.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aws.s3.service.ImageFetcher;

@RestController
@RequestMapping("/get-image")
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageFetcher imageFetcher;

    public ImageController(ImageFetcher imageFetcher) {
        this.imageFetcher = imageFetcher;
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @GetMapping
    public ResponseEntity<List<String>> getImage() {
        logger.info("getImage method start");
        ResponseEntity<List<String>> responseEntity = new ResponseEntity<>(imageFetcher.getImage(), HttpStatus.OK);
        logger.info("getImage method end");
        return responseEntity;
    }

}