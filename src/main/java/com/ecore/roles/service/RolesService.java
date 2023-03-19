package com.ecore.roles.service;

import com.ecore.roles.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RolesService {

    Role createRole(Role role);

    Role getRole(UUID id);

    Page<Role> getRoles(Pageable pageable);

    Role getRoleByUserIdAndTeamId(UUID userId, UUID teamId);
}
