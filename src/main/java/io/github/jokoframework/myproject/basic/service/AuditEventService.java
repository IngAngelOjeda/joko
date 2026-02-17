package io.github.jokoframework.myproject.basic.service;

import io.github.jokoframework.myproject.basic.dto.AuditEventListRequestDTO;
import io.github.jokoframework.myproject.basic.entities.AuditEventEntity;
import io.github.jokoframework.myproject.basic.enums.AuditEventEnum;
import org.springframework.data.domain.Page;

public interface AuditEventService {

    Page<AuditEventEntity> listAuditEvents(AuditEventListRequestDTO requestDTO, int page, int size);

    AuditEventEntity saveEvent(AuditEventEnum action,
                               String resourceType,
                               String resourceId,
                               String newMetadata,
                               String oldMetadata);
}
