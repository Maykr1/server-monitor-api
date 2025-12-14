package com.eclark.server_monitor_api.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import com.eclark.server_monitor_api.model.external.ExternalServiceResponse;
import com.eclark.server_monitor_api.service.impl.ExternalServicesServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ExternalServicesServiceTests {
    
    @Mock
    private RestClient restClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersUriSpec requestSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersSpec headersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private ExternalServicesServiceImpl service;

    private ExternalServiceResponse mockResponse;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        mockResponse = new ExternalServiceResponse();
        
        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ExternalServiceResponse.class))
                .thenReturn(mockResponse);
    }


    @Test
    void getGithubStatus_returnsResponse() {
        ExternalServiceResponse response = service.getGithubStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://www.githubstatus.com/api/v2/summary.json");
    }

    @Test
    void getOpenAIStatus_returnsResponse() {
        ExternalServiceResponse response = service.getOpenAIStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://status.openai.com/api/v2/summary.json");
    }

    @Test
    void getAtlassianStatus_returnsResponse() {
        ExternalServiceResponse response = service.getAtlassianStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://www.atlassian-status.com/api/v2/summary.json");
    }

    @Test
    void getDockerStatus_returnsResponse() {
        ExternalServiceResponse response = service.getDockerStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://www.dockerstatus.com/api/v2/summary.json");
    }

    @Test
    void getCloudflareStatus_returnsResponse() {
        ExternalServiceResponse response = service.getCloudflareStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://www.cloudflarestatus.com/api/v2/summary.json");
    }

    @Test
    void getGCloudStatus_returnsResponse() {
        ExternalServiceResponse response = service.getGCloudStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://status.cloud.google.com/api/v2/summary.json");
    }

    @Test
    void getDiscordStatus_returnsResponse() {
        ExternalServiceResponse response = service.getDiscordStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://status.discord.com/api/v2/summary.json");
    }

    @Test
    void getSplunkStatus_returnsResponse() {
        ExternalServiceResponse response = service.getSplunkStatus();

        assertNotNull(response);
        assertSame(mockResponse, response);

        verify(requestSpec).uri("https://status.splunk.com/api/v2/summary.json");
    }
}
