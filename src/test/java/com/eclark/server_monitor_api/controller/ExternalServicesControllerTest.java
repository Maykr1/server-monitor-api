package com.eclark.server_monitor_api.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eclark.server_monitor_api.model.external.ExternalServiceResponse;
import com.eclark.server_monitor_api.service.ExternalServicesService;

@WebMvcTest(ExternalServicesController.class)
public class ExternalServicesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExternalServicesService externalServicesService;

    private ExternalServiceResponse mockResponse;

    @BeforeEach
        void setUp() {
        mockResponse = new ExternalServiceResponse();
    }

    @Test
    void getGithubStatus_returns200() throws Exception {
        when(externalServicesService.getGithubStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/github"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getGithubStatus();
    }

    @Test
    void getOpenAIStatus_returns200() throws Exception {
        when(externalServicesService.getOpenAIStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/openai"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getOpenAIStatus();
    }

    @Test
    void getAtlassianStatus_returns200() throws Exception {
        when(externalServicesService.getAtlassianStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/atlassian"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getAtlassianStatus();
    }

    @Test
    void getDockerStatus_returns200() throws Exception {
        when(externalServicesService.getDockerStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/docker"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getDockerStatus();
    }

    @Test
    void getCloudflareStatus_returns200() throws Exception {
        when(externalServicesService.getCloudflareStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/cloudflare"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getCloudflareStatus();
    }

    @Test
    void getGCloudStatus_returns200() throws Exception {
        when(externalServicesService.getGCloudStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/gcloud"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getGCloudStatus();
    }

    @Test
    void getDiscordStatus_returns200() throws Exception {
        when(externalServicesService.getDiscordStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/discord"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getDiscordStatus();
    }

    @Test
    void getSplunkStatus_returns200() throws Exception {
        when(externalServicesService.getSplunkStatus())
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-services/splunk"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(externalServicesService).getSplunkStatus();
    }
}
