package com.eclark.server_monitor_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eclark.server_monitor_api.model.external.ExternalServiceResponse;
import com.eclark.server_monitor_api.service.ExternalServicesService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/external-services")
public class ExternalServicesController {
    private static final Logger logger = LoggerFactory.getLogger(ExternalServicesController.class);
    private final ExternalServicesService externalServicesService;

    @GetMapping("/github")
    public ResponseEntity<ExternalServiceResponse> getGithubStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving Github Status");

        ExternalServiceResponse response = externalServicesService.getGithubStatus();

        logger.info("[{} ms] - Finished Retrieving GithubStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/openai")
    public ResponseEntity<ExternalServiceResponse> getOpenAIStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving OpenAI Status");

        ExternalServiceResponse response = externalServicesService.getOpenAIStatus();

        logger.info("[{} ms] - Finished Retrieving OpenAIStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/atlassian")
    public ResponseEntity<ExternalServiceResponse> getAtlassianStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving Atlassian Status");

        ExternalServiceResponse response = externalServicesService.getAtlassianStatus();

        logger.info("[{} ms] - Finished Retrieving AtlassianStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/docker")
    public ResponseEntity<ExternalServiceResponse> getDockerStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving Docker Status");

        ExternalServiceResponse response = externalServicesService.getDockerStatus();

        logger.info("[{} ms] - Finished Retrieving DockerStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/cloudflare")
    public ResponseEntity<ExternalServiceResponse> getCloudflareStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving Cloudflare Status");

        ExternalServiceResponse response = externalServicesService.getCloudflareStatus();

        logger.info("[{} ms] - Finished Retrieving CloudflareStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/gcloud")
    public ResponseEntity<ExternalServiceResponse> getGCloudStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving GCloud Status");

        ExternalServiceResponse response = externalServicesService.getGCloudStatus();

        logger.info("[{} ms] - Finished Retrieving GCloudStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/discord")
    public ResponseEntity<ExternalServiceResponse> getDiscordStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving Discord Status");

        ExternalServiceResponse response = externalServicesService.getDiscordStatus();

        logger.info("[{} ms] - Finished Retrieving DiscordStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/splunk")
    public ResponseEntity<ExternalServiceResponse> getSplunkStatus() {
        long start = System.currentTimeMillis();
        logger.info("Retrieving Splunk Status");

        ExternalServiceResponse response = externalServicesService.getSplunkStatus();

        logger.info("[{} ms] - Finished Retrieving SplunkStatus", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}