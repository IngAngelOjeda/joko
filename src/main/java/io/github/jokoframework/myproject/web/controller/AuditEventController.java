package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.basic.dto.AuditEventListRequestDTO;
import io.github.jokoframework.myproject.basic.entities.AuditEventEntity;
import io.github.jokoframework.myproject.basic.enums.AuditEventEnum;
import io.github.jokoframework.myproject.basic.service.AuditEventService;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.github.jokoframework.security.controller.SecurityConstants;

import java.time.Instant;

@RestController
public class AuditEventController {

    public static final String JOKO_STARTER_KIT_VERSION_HEADER = "X-JOKO-STARTER-KIT-VERSION";
    public static final String JOKO_STARTER_KIT_VERSION = "1.0";

    @Autowired
    private AuditEventService auditEventService;

    @ApiOperation(
            value = "Listar eventos de auditoría",
            notes = "Permite filtrar por action, resourceType, resourceId y por rango de fechas (from/to). Incluye paginación.",
            position = 1
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Eventos de auditoría recuperados exitosamente"),
            @ApiResponse(code = 401, message = "No autenticado"),
            @ApiResponse(code = 403, message = "No autorizado")
    })
    @RequestMapping(
            value = ApiPaths.AUDIT_EVENTS_LIST,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Token de acceso del usuario"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Versión", defaultValue = JOKO_STARTER_KIT_VERSION),

    })
    public ResponseEntity<?> listAuditEvents(
            @ApiParam(value = "Acción a filtrar", example = "USER_CREATED")
            @RequestParam(value = "action", required = false) AuditEventEnum action,

            @ApiParam(value = "Tipo de recurso", example = "USER")
            @RequestParam(value = "resourceType", required = false) String resourceType,

            @ApiParam(value = "ID del recurso", example = "123")
            @RequestParam(value = "resourceId", required = false) String resourceId,

            @ApiParam(value = "Fecha/hora desde (ISO-8601)", example = "2026-02-01T00:00:00Z")
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,

            @ApiParam(value = "Fecha/hora hasta (ISO-8601)", example = "2026-02-16T23:59:59Z")
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,

            @ApiParam(value = "Número de página (0..N)", example = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,

            @ApiParam(value = "Tamaño de página", example = "20")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,

            @ApiParam(value = "Orden ascendente por createdAt (true=ASC, false=DESC). Default: DESC", example = "false")
            @RequestParam(value = "orderAsc", required = false) Boolean orderAsc
    ) {
        AuditEventListRequestDTO request = new AuditEventListRequestDTO();
        request.setAction(action);
        request.setResourceType(resourceType);
        request.setResourceId(resourceId);
        request.setFrom(from);
        request.setTo(to);
        request.setOrder(orderAsc);

        Page<AuditEventEntity> result = auditEventService.listAuditEvents(request, page, size);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
