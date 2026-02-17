package io.github.jokoframework.myproject.web.controller;


import io.github.jokoframework.myproject.web.request.UserRequestDTO;
import io.github.jokoframework.myproject.web.response.ServiceResponseDTO;
import io.swagger.annotations.*;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.manager.UserManager;
import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.myproject.web.request.CsvExportRequestDTO;
import io.github.jokoframework.myproject.web.response.HeartBeatResponseDTO;
import io.github.jokoframework.myproject.web.response.UserResponseDTO;
import io.github.jokoframework.myproject.web.response.UsersResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Users API
 *
 * @author bsandoval
 */
@RestController
public class UserController extends BaseRestController {

    @Autowired
    private UserManager userManager;

    @Override
    @RequestMapping(value = ApiPaths.USERS_HEARTBEAT, method = RequestMethod.GET)
    public ResponseEntity<HeartBeatResponseDTO> getHearbeat() {
        return new ResponseEntity<HeartBeatResponseDTO>(getHeartBeatStatus(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get user's profile",
            notes = "Get user's profile based on username", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User profile retrieved"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = ApiPaths.USERS_BY_NAME, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UserResponseDTO> getUser(@ApiParam("user name") @PathVariable("username") String username)
            throws UserException {
        UserDTO userDTO = userManager.getByUsername(username);
        UserResponseDTO response = new UserResponseDTO(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Get users",
            notes = "Get all users", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Users retrieved"),
            @ApiResponse(code = 404, message = "Users not found")
    })
    @RequestMapping(value = ApiPaths.ROOT_USERS, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UsersResponseDTO> getUsers() throws UserException {
        List<UserDTO> list = userManager.findAll();
        UsersResponseDTO response = new UsersResponseDTO(list);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Export user's list to csv",
            notes = "Get all users and creates a csv file", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Csv successfully created"),
            @ApiResponse(code = 404, message = "Users not found")
    })
    @RequestMapping(value = ApiPaths.USERS_CSV, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UsersResponseDTO> getUsersCsvList(@RequestBody @Valid CsvExportRequestDTO request) throws UserException {
        List<UserDTO> list = userManager.findAll();
        byte[] csv = userManager.exportUsersListToCsv(list, request.getColumns());
        
        UsersResponseDTO response = new UsersResponseDTO(list);
        response.setCsv(csv);
        response.setSuccess(true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Create user",
            notes = "Creates a new user", position = 3)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 409, message = "User already exists")
    })
    @RequestMapping(value = ApiPaths.USER_SAVE, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Valid UserRequestDTO request
    ) throws UserException {

        UserDTO dto = new UserDTO();
        dto.setUsername(request.getUsername());
        dto.setLastName(request.getLastName());

        UserDTO created = userManager.save(dto);

        UserResponseDTO response = new UserResponseDTO(created);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update user",
            notes = "Updates an existing user (name/lastName) using username", position = 4)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = ApiPaths.USER_UPDATE, method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @RequestBody @Valid UserRequestDTO request
    ) throws UserException {

        UserDTO dto = new UserDTO();
        dto.setUsername(request.getUsername());
        dto.setName(request.getName());
        dto.setLastName(request.getLastName());

        UserDTO updated = userManager.save(dto);

        UserResponseDTO response = new UserResponseDTO(updated);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete user by id (hard delete)",
            notes = "Deletes a user permanently using its id", position = 6)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = ApiPaths.USER_DELETE_BY_ID, method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<ServiceResponseDTO> deleteUserById(
            @ApiParam(value = "User id", example = "1")
            @PathVariable("id") Long id
    ) throws UserException {

        userManager.deleteById(id);

        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setSuccess(true);
        response.setMessage("User deleted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}