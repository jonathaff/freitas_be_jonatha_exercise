package com.ecore.roles.service.impl;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.client.model.User;
import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.InvalidMembershipException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.service.TeamsService;
import com.ecore.roles.service.UsersService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Log4j2
@Service
public class MembershipsServiceImpl implements MembershipsService {

    private final MembershipRepository membershipRepository;
    private final RoleRepository roleRepository;
    private final UsersService usersService;
    private final TeamsService teamsService;

    @Autowired
    public MembershipsServiceImpl(
            MembershipRepository membershipRepository,
            RoleRepository roleRepository,
            UsersService usersService,
            TeamsService teamsService) {
        this.membershipRepository = membershipRepository;
        this.roleRepository = roleRepository;
        this.usersService = usersService;
        this.teamsService = teamsService;
    }

    @Override
    public Membership assignRoleToMembership(@NonNull Membership membership) {

        final UUID roleId = ofNullable(membership.getRole()).map(Role::getId)
                .orElseThrow(() -> new InvalidArgumentException(Role.class));

        final Optional<Team> team = ofNullable(teamsService.getTeam(membership.getTeamId()));

        if (team.isEmpty()) {
            throw new ResourceNotFoundException(Team.class, membership.getTeamId());
        }

        if (isNotEmpty(team.get()) && !team.get().getTeamMemberIds().contains(membership.getUserId())) {
            throw new InvalidMembershipException(Membership.class, membership);
        }

        ofNullable(usersService.getUser(membership.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException(User.class, membership.getUserId()));

        if (!membershipRepository.findRolesByUserIdAndTeamId(membership.getUserId(), membership.getTeamId(), PageRequest.of(0, 20)).getContent().isEmpty()) {
            throw new ResourceExistsException(Membership.class, membership);
        }

        roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(Role.class, roleId));
        return membershipRepository.save(membership);
    }

    @Override
    public Page<Membership> getMembershipsByRoleId(@NonNull UUID roleId, Pageable pageable) {
        return membershipRepository.findByRoleId(roleId, pageable);
    }

    @Override
    public Membership getMembership(UUID membershipId) {
        return membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException(Membership.class, membershipId));
    }

    @Override
    public Page<Membership> getMemberships(Pageable pageable) {
        return membershipRepository.findAll(pageable);
    }
}
