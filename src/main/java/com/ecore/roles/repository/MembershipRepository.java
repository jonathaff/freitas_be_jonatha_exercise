package com.ecore.roles.repository;

import com.ecore.roles.model.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    Page<Membership> findRolesByUserIdAndTeamId(UUID userId, UUID teamId, Pageable pageable);

    Page<Membership> findByRoleId(UUID roleId, Pageable pageable);
}
