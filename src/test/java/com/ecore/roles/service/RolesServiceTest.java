package com.ecore.roles.service;

import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.impl.RolesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.ecore.roles.utils.TestData.DEFAULT_MEMBERSHIP;
import static com.ecore.roles.utils.TestData.DEVELOPER_ROLE;
import static com.ecore.roles.utils.TestData.UUID_1;
import static com.ecore.roles.utils.TestData.UUID_2;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolesServiceTest {

    @InjectMocks
    private RolesServiceImpl rolesService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MembershipsService membershipsService;

    @Test
    public void shouldCreateRole() {
        Role developerRole = DEVELOPER_ROLE();
        when(roleRepository.save(developerRole)).thenReturn(developerRole);

        Role role = rolesService.createRole(developerRole);

        assertNotNull(role);
        assertEquals(developerRole, role);
    }

    @Test
    public void shouldFailToCreateRoleWhenRoleIsNull() {
        assertThrows(NullPointerException.class,
                () -> rolesService.createRole(null));
    }

    @Test
    public void shouldReturnRoleWhenRoleIdExists() {
        Role developerRole = DEVELOPER_ROLE();
        when(roleRepository.findById(developerRole.getId())).thenReturn(of(developerRole));

        Role role = rolesService.getRole(developerRole.getId());

        assertNotNull(role);
        assertEquals(developerRole, role);
    }

    @Test
    public void shouldFailToGetRoleWhenRoleIdDoesNotExist() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.getRole(UUID_1));

        assertEquals(format("Role %s not found", UUID_1), exception.getMessage());
    }

    @Test
    public void shouldGetAllRoles() {
        final Page<Role> expectedRoles = mock(Page.class);
        final Pageable pageable = mock(Pageable.class);

        when(roleRepository.findAll(pageable)).thenReturn(expectedRoles);
        final Page<Role> roles = rolesService.getRoles(pageable);

        assertEquals(expectedRoles, roles);
        verify(roleRepository, times(1)).findAll(pageable);
    }

    @Test
    public void shouldFailToGetUnknownUserAndTeam() {
        when(membershipRepository.findByUserIdAndTeamId(UUID_1, UUID_2)).thenReturn(empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.getRoleByUserIdAndTeamId(UUID_1, UUID_2));
        assertEquals(
                "Role Combined UUIDs (11111111-1111-1111-1111-111111111111 and 22222222-2222-2222-2222-222222222222) not found",
                exception.getMessage());
    }

    @Test
    public void shouldGetRoleByUserIdAndTeamId() {
        final Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(membershipRepository.findByUserIdAndTeamId(UUID_1, UUID_2)).thenReturn(of(expectedMembership));
        final Role role = rolesService.getRoleByUserIdAndTeamId(UUID_1, UUID_2);

        assertEquals(expectedMembership.getRole(), role);
        verify(membershipRepository, times(1)).findByUserIdAndTeamId(UUID_1, UUID_2);
    }
}
