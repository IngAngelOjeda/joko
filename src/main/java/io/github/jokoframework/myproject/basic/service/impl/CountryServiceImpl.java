package io.github.jokoframework.myproject.basic.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jokoframework.myproject.basic.dto.CountryDTO;
import io.github.jokoframework.myproject.basic.entities.CountryEntity;
import io.github.jokoframework.myproject.basic.enums.AuditEventEnum;
import io.github.jokoframework.myproject.basic.repositories.CountryRepository;
import io.github.jokoframework.myproject.basic.service.AuditEventService;
import io.github.jokoframework.myproject.basic.service.CountryService;
import io.github.jokoframework.utils.dto_mapping.DTOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    private AuditEventService auditEventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository){
        this.countryRepository = countryRepository;
    }

    @Override
    public List<CountryDTO> listCountries() {
        List<CountryEntity> entityList = countryRepository.findAll();
        return DTOUtils.fromEntityToDTO(entityList, CountryDTO.class);
    }

    @Override
    public CountryEntity save(CountryDTO countryDTO) {

        String countryId = countryDTO != null ? countryDTO.getId() : null;
        if (!StringUtils.hasText(countryId)) {
            throw new IllegalArgumentException("Country id is required");
        }

        Optional<CountryEntity> opt = countryRepository.findById(countryId);
        CountryEntity existing = opt.orElse(null);

        boolean isCreate = (existing == null);
        String oldMetadata = isCreate ? null : toMetadata(existing);

        CountryEntity entity = isCreate ? new CountryEntity() : existing;
        entity.setId(countryId);
        entity.setDescription(countryDTO.getDescription());

        CountryEntity saved = countryRepository.save(entity);

        String newMetadata = toMetadata(saved);

        auditEventService.saveEvent(
                isCreate ? AuditEventEnum.USER_CREATED : AuditEventEnum.USER_UPDATED,
                "COUNTRY",
                countryId,
                newMetadata,
                oldMetadata
        );

        return saved;
    }

    @Override
    public void delete(String id) {

        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Country id is required");
        }

        CountryEntity existing = countryRepository.findById(id).orElse(null);
        String oldMetadata = existing != null ? toMetadata(existing) : null;

        countryRepository.deleteById(id);

        auditEventService.saveEvent(
                AuditEventEnum.USER_DELETED,
                "COUNTRY",
                id,
                null,
                oldMetadata
        );
    }

    private String toMetadata(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\":\"metadata_serialization_failed\"}";
        }
    }
}
