package com.eclark.server_monitor_api.model.external;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalServiceResponse {
    private Page page;
    private List<Component> components;
    private Status status;
}
