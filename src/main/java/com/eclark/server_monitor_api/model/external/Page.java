package com.eclark.server_monitor_api.model.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page {
    private String id;
    private String name;
    private String url;

    @JsonProperty("time_zone")
    private String timeZone;

    @JsonProperty("updated_at")
    private String updatedAt;
}
