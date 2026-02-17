package io.github.jokoframework.myproject.basic.entities;

import io.github.jokoframework.myproject.basic.enums.AuditEventEnum;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "audit_event", schema = "basic",
indexes = {
@Index(name = "idx_audit_created_at", columnList = "created_at"),
@Index(name = "idx_audit_action", columnList = "action"),
@Index(name = "idx_audit_resource_type", columnList = "resource_type"),
@Index(name = "idx_audit_resource_id", columnList = "resource_id"),
@Index(name = "idx_audit_correlation_id", columnList = "correlation_id")
  })
public class AuditEventEntity {

    @GenericGenerator(
            name = "auditEventSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "audit_event_id_seq"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "auditEventSequenceGenerator")
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private Long id;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "actor")
    private String actor;

    @Column(name = "action")
    private AuditEventEnum action;

    @Column(name = "resource_type", nullable = false, length = 64)
    private String resourceType;

    @Column(name = "resource_id", nullable = false, length = 64)
    private String resourceId;

    @Lob
    @Column(name = "old_metadata")
    private String oldMetadata;

    @Lob
    @Column(name = "new_metadata")
    private String newMetadata;

    @Column(name = "correlation_id", nullable = false, length = 128)
    private String correlationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

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

    public String getOldMetadata() {
        return oldMetadata;
    }

    public void setOldMetadata(String oldMetadata) {
        this.oldMetadata = oldMetadata;
    }

    public String getNewMetadata() {
        return newMetadata;
    }

    public void setNewMetadata(String newMetadata) {
        this.newMetadata = newMetadata;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
