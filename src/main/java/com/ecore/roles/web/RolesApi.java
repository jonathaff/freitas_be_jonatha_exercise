package com.ecore.roles.web;

import com.ecore.roles.web.dto.RoleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface RolesApi {

    ResponseEntity<RoleDto> createRole(
            RoleDto role);

    ResponseEntity<List<RoleDto>> getRoles(Pageable pageable);

    ResponseEntity<RoleDto> getRole(
            UUID roleId);

    ResponseEntity<List<RoleDto>> getRoleByUserIdAndTeamId(
            UUID userId,
            UUID teamId,
            Pageable pageable);

}
