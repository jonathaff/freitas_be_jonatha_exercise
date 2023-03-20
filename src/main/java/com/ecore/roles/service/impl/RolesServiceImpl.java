package com.ecore.roles.service.impl;

import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.RolesService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@Service
public class RolesServiceImpl implements RolesService {

    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;

    @Autowired
    public RolesServiceImpl(
            RoleRepository roleRepository,
            MembershipRepository membershipRepository) {
        this.roleRepository = roleRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    public Role createRole(@NonNull Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new ResourceExistsException(Role.class, role);
        }
        return roleRepository.save(role);
    }

    @Override
    public Role getRole(@NonNull UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, id));
    }

    @Override
    public Page<Role> getRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Override
    public Page<Role> getRolesByUserIdAndTeamId(UUID userId, UUID teamId, Pageable pageable) {
        return membershipRepository.findRolesByUserIdAndTeamId(userId, teamId, pageable)
                .map(Membership::getRole);
    }

}
