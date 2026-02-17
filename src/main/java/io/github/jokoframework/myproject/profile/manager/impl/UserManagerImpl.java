package io.github.jokoframework.myproject.profile.manager.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jokoframework.myproject.basic.enums.AuditEventEnum;
import io.github.jokoframework.myproject.basic.service.AuditEventService;
import io.github.jokoframework.myproject.mapper.OrikaBeanMapper;
import io.github.jokoframework.myproject.profile.manager.UserManager;
import io.github.jokoframework.security.util.SecurityUtils;
import io.github.jokoframework.myproject.profile.entities.UserEntity;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.service.UserService;
import io.github.jokoframework.myproject.web.dto.UserAuthDTO;
import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.utils.csv.CsvUtils;
import io.github.jokoframework.utils.dto_mapping.DTOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManagerImpl implements UserManager {

	@Autowired
	private UserService userService;

    @Autowired
    private OrikaBeanMapper mapper;

    @Autowired
    private AuditEventService auditEventService;

    @Autowired
    private ObjectMapper objectMapper;

	@Override
    public List<UserDTO> findAll() {
        return DTOUtils.fromEntityToDTO(userService.findAll(), UserDTO.class);
    }

    @Override
    public UserDTO getByUsername(String username) throws UserException {
        UserEntity user = userService.getByUsername(username);

        if (user != null) {
            return mapper.map(user, UserDTO.class);
        }
        else {
            throw UserException.userNotFound(username);
        }
    }

    @Override
    public boolean checkEndUser(String username, String password) {
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            return  false;
        }

        UserEntity entity = userService.getByUsername(username);

        return entity != null && SecurityUtils.matchPassword(password, entity.getPassword());
    }

    @Override
    public UserAuthDTO checkUser(String username, String password) throws UserException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            throw UserException.invalidUserPassword();
        }

        UserAuthDTO user = mapper.map(userService.getByUsername(username), UserAuthDTO.class);

        if(user == null){
            throw UserException.invalidUserPassword();
        }

        if(!SecurityUtils.matchPassword(password, user.getPassword())){
            throw UserException.invalidUserPassword();
        }

        return user;
    }

    @Override
    public byte[] exportUsersListToCsv(List<UserDTO> list, List<String> columns) {
        return CsvUtils.convertToCsv(list, columns, UserDTO.class);
    }

    @Override
    public UserDTO save(UserDTO user) throws UserException {

        // Verificar si existe
        UserEntity existing = userService.getByUsername(user.getUsername());
        boolean isCreate = true;

        if(existing == null) {
        	isCreate = false;
        }

        String oldMetadata = isCreate ? null : toMetadata(existing);

        // Guardar
        UserEntity entity = mapper.map(user, UserEntity.class);

        UserEntity saved = userService.save(entity);

        String newMetadata = toMetadata(saved);

        String resourceId = saved.getId() != null
                ? String.valueOf(saved.getId())
                : saved.getUsername();

        auditEventService.saveEvent(
                isCreate ? AuditEventEnum.USER_CREATED : AuditEventEnum.USER_UPDATED,
                "USER",
                resourceId,
                newMetadata,
                oldMetadata
        );

        return mapper.map(saved, UserDTO.class);
    }

    @Override
    public void deleteById(Long userId) throws UserException {

        UserEntity existing = userService.getById(userId);
        if (existing == null) {
            throw UserException.userNotFound(String.valueOf(userId));
        }

        String oldMetadata = toMetadata(existing);

        userService.deleteById(userId);

        auditEventService.saveEvent(
                AuditEventEnum.USER_DELETED,
                "USER",
                String.valueOf(userId),
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
