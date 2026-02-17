package io.github.jokoframework.myproject.basic.service.impl;

import io.github.jokoframework.myproject.basic.dto.AuditEventListRequestDTO;
import io.github.jokoframework.myproject.basic.entities.AuditEventEntity;
import io.github.jokoframework.myproject.basic.enums.AuditEventEnum;
import io.github.jokoframework.myproject.basic.repositories.AuditEventRepository;
import io.github.jokoframework.myproject.basic.service.AuditEventService;
import io.github.jokoframework.myproject.basic.specifications.AuditEventSpecification;
import io.github.jokoframework.myproject.security.SecurityContextHelper;
import io.github.jokoframework.myproject.web.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
public class AuditEventServiceImpl implements AuditEventService {

    private static final Logger log = LoggerFactory.getLogger(AuditEventServiceImpl.class);

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    SecurityContextHelper securityContextHelper;


    public Page<AuditEventEntity> listAuditEvents(AuditEventListRequestDTO request, int page, int size) {

        Sort sort = Sort.by("createdAt").descending();
        if (Boolean.TRUE.equals(request.isOrder())) {
            sort = Sort.by("createdAt").ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return auditEventRepository.findAll(AuditEventSpecification.filter(request), pageable);
    }

    @Override
    public AuditEventEntity saveEvent(AuditEventEnum action,
                                      String resourceType,
                                      String resourceId,
                                      String newMetadata,
                                      String oldMetadata) {

        // Obtener correlationId desde el RequestContext
        String correlationId = RequestContext.getCorrelationId();

        if (!StringUtils.hasText(correlationId)) {
            throw new IllegalStateException("Missing correlationId in RequestContext");
        }

        if (action == null) {
            throw new IllegalArgumentException("action is required");
        }

        if (!StringUtils.hasText(resourceType)) {
            throw new IllegalArgumentException("resourceType is required");
        }

        if (!StringUtils.hasText(resourceId)) {
            throw new IllegalArgumentException("resourceId is required");
        }

        String actor = securityContextHelper.getCurrentUsername();

        AuditEventEntity event = new AuditEventEntity();
        event.setCreatedAt(Instant.now());
        event.setActor(StringUtils.hasText(actor) ? actor : null);
        event.setAction(action);
        event.setResourceType(resourceType);
        event.setResourceId(resourceId);
        event.setOldMetadata(StringUtils.hasText(oldMetadata) ? oldMetadata : null);
        event.setNewMetadata(StringUtils.hasText(newMetadata) ? newMetadata : null);
        event.setCorrelationId(correlationId);

        AuditEventEntity saved = auditEventRepository.save(event);

        log.info("audit_event created correlationId={} action={} resourceType={} resourceId={} actor={}",
                correlationId, action, resourceType, resourceId, actor);

        return saved;
    }

}
