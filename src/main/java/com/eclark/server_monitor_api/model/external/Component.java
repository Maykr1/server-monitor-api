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
public class Component {
    private String id;
    private String name;
    private String status;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
    
    private Integer position;
    private String description;
    private Boolean showcase;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("page_id")
    private String pageId;

    private Boolean group;

    @JsonProperty("only_show_if_degraded")
    private Boolean onlyShowIfDegraded;
    
    private Component component;
}
