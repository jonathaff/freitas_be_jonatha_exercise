package com.ecore.roles.service;

import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MembershipsService {

    Membership assignRoleToMembership(Membership membership) throws ResourceNotFoundException;

    Page<Membership> getMembershipsByRoleId(UUID roleId, Pageable pageable);

    Membership getMembership(UUID membershipId);

    Page<Membership> getMemberships(Pageable pageable);
}
