package com.eclark.server_monitor_api.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.eclark.server_monitor_api.model.external.ExternalServiceResponse;
import com.eclark.server_monitor_api.service.ExternalServicesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalServicesServiceImpl implements ExternalServicesService {
    private final RestClient restClient;

    @Override
    public ExternalServiceResponse getGithubStatus() {
        return restClient.get()
            .uri("https://www.githubstatus.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }
    
    @Override
    public ExternalServiceResponse getOpenAIStatus() {
        return restClient.get()
            .uri("https://status.openai.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }

    @Override
    public ExternalServiceResponse getAtlassianStatus() {
        return restClient.get()
            .uri("https://www.atlassian-status.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }
    
    @Override
    public ExternalServiceResponse getDockerStatus() {
        return restClient.get()
            .uri("https://www.dockerstatus.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }

    @Override
    public ExternalServiceResponse getCloudflareStatus() {
        return restClient.get()
            .uri("https://www.cloudflarestatus.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }

    @Override
    public ExternalServiceResponse getGCloudStatus() {
        return restClient.get()
            .uri("https://status.cloud.google.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }

    @Override
    public ExternalServiceResponse getDiscordStatus() {
        return restClient.get()
            .uri("https://status.discord.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }

    @Override
    public ExternalServiceResponse getSplunkStatus() {
        return restClient.get()
            .uri("https://status.splunk.com/api/v2/summary.json")
            .retrieve()
            .body(ExternalServiceResponse.class);
    }
}
