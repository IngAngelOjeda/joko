package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.basic.dto.CountryDTO;
import io.github.jokoframework.myproject.basic.entities.CountryEntity;
import io.github.jokoframework.myproject.basic.service.CountryService;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CountryController {

    private final CountryService basicService;

    @Autowired
    public CountryController(CountryService basicService){
        this.basicService = basicService;
    }

    @ApiOperation(value = "Lista paises",
            notes = "Lista de paises registrados o vacio si no hay nada",
            position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Paises disponibles")
    })
    @RequestMapping(value = ApiPaths.COUNTRIES, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
            paramType = "header", required = false, value = "Access Token")
    public List<CountryDTO> listAll(){
        return basicService.listCountries();
    }

    @ApiOperation(value = "Crear pais",
            notes = "Crea un nuevo pais", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pais creado correctamente")
    })
    @RequestMapping(value = ApiPaths.COUNTRIES, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
            paramType = "header", required = false, value = "Access Token")
    public CountryEntity saveCountry(@RequestBody CountryDTO request){
        return basicService.save(request);
    }

    @ApiOperation(value = "Actualizar pais",
            notes = "Actualiza un pais existente (usa el id del DTO)", position = 3)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pais actualizado correctamente"),
            @ApiResponse(code = 400, message = "Request inválido")
    })
    @RequestMapping(value = ApiPaths.COUNTRIES, method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
            paramType = "header", required = false, value = "Access Token")
    public CountryEntity updateCountry(@RequestBody CountryDTO request){
        return basicService.save(request);
    }

    @ApiOperation(value = "Eliminar pais",
            notes = "Elimina un pais por id (hard delete)", position = 4)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pais eliminado correctamente"),
            @ApiResponse(code = 404, message = "Pais no encontrado")
    })
    @RequestMapping(value = ApiPaths.COUNTRIES + "/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
            paramType = "header", required = false, value = "Access Token")
    public void deleteCountry(@ApiParam(value = "ID del pais", example = "AR") @PathVariable("id") String id){
        basicService.delete(id);
    }

}
