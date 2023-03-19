package com.ecore.roles.web.rest;

import com.ecore.roles.service.RolesService;
import com.ecore.roles.web.RolesApi;
import com.ecore.roles.web.dto.RoleDto;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecore.roles.web.dto.RoleDto.fromModel;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/roles")
public class RolesRestController implements RolesApi {

    private final RolesService rolesService;

    @Override
    @PostMapping(
            consumes = {APPLICATION_JSON_VALUE},
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "RolesRestController.createRole",
            description = "Time taken to execute 'create role' request")
    public ResponseEntity<RoleDto> createRole(
            @Valid @RequestBody RoleDto role) {
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(fromModel(rolesService.createRole(role.toModel())));
    }

    @Override
    @GetMapping(
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "RolesRestController.getRoles", description = "Time taken to execute 'get roles' request")
    public ResponseEntity<List<RoleDto>> getRoles(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(rolesService.getRoles(pageable)
                        .stream()
                        .map(RoleDto::fromModel)
                        .collect(Collectors.toList()));
    }

    @Override
    @GetMapping(
            path = "/{roleId}",
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "RolesRestController.getRole",
            description = "Time taken to execute 'get specific role' request")
    public ResponseEntity<RoleDto> getRole(
            @PathVariable UUID roleId) {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(fromModel(rolesService.getRole(roleId)));
    }

    @Override
    @GetMapping(
            path = "/search",
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "RolesRestController.getRoleByUserIdAndTeamId",
            description = "Time taken to execute 'get role by userId and TeamId' request")
    public ResponseEntity<RoleDto> getRoleByUserIdAndTeamId(
            @RequestParam UUID teamMemberId,
            @RequestParam UUID teamId) {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(fromModel(rolesService.getRoleByUserIdAndTeamId(teamMemberId, teamId)));
    }

}
