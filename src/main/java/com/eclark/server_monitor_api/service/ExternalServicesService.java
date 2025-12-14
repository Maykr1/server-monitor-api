package com.eclark.server_monitor_api.service;

import com.eclark.server_monitor_api.model.external.ExternalServiceResponse;

public interface ExternalServicesService {
    public ExternalServiceResponse getGithubStatus();
    public ExternalServiceResponse getOpenAIStatus();
    public ExternalServiceResponse getAtlassianStatus();
    public ExternalServiceResponse getDockerStatus();
    public ExternalServiceResponse getCloudflareStatus();
    public ExternalServiceResponse getGCloudStatus();
    public ExternalServiceResponse getDiscordStatus();
    public ExternalServiceResponse getSplunkStatus();
}
