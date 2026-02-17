package io.github.jokoframework.myproject.basic.dto;

import io.github.jokoframework.myproject.basic.enums.AuditEventEnum;

import java.time.Instant;

public class AuditEventListRequestDTO {

    private AuditEventEnum action;
    private String resourceType;
    private String resourceId;
    private Instant from;
    private Instant to;
    private Boolean order;

    public AuditEventEnum getAction() {
        return action;
    }

    public void setAction(AuditEventEnum action) {
        this.action = action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public Boolean isOrder() {
        return order;
    }

    public void setOrder(Boolean order) {
        this.order = order;
    }

}
