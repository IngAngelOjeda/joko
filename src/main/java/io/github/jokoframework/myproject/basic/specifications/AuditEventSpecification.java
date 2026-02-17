package io.github.jokoframework.myproject.basic.specifications;

import io.github.jokoframework.myproject.basic.dto.AuditEventListRequestDTO;
import io.github.jokoframework.myproject.basic.entities.AuditEventEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AuditEventSpecification {

    public static Specification<AuditEventEntity> filter(AuditEventListRequestDTO request) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getAction() != null) {
                predicates.add(cb.equal(root.get("action"), request.getAction()));
            }

            if (request.getResourceType() != null && !request.getResourceType().isEmpty()) {
                predicates.add(cb.equal(root.get("resourceType"), request.getResourceType()));
            }

            if (request.getResourceId() != null && !request.getResourceId().isEmpty()) {
                predicates.add(cb.equal(root.get("resourceId"), request.getResourceId()));
            }

            if (request.getFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getFrom()));
            }

            if (request.getTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
