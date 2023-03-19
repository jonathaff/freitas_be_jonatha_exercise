package com.ecore.roles.service;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.InvalidMembershipException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.impl.MembershipsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static com.ecore.roles.utils.TestData.DEFAULT_MEMBERSHIP;
import static com.ecore.roles.utils.TestData.DEVELOPER_ROLE;
import static com.ecore.roles.utils.TestData.GIANNI_USER;
import static com.ecore.roles.utils.TestData.ORDINARY_CORAL_LYNX_TEAM;
import static com.ecore.roles.utils.TestData.UUID_1;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipsServiceTest {

    @InjectMocks
    private MembershipsServiceImpl membershipsService;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UsersService usersService;
    @Mock
    private TeamsService teamsService;

    @Test
    public void shouldCreateMembership() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(teamsService.getTeam(expectedMembership.getTeamId())).thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(usersService.getUser(expectedMembership.getUserId())).thenReturn(GIANNI_USER());
        when(roleRepository.findById(expectedMembership.getRole().getId()))
                .thenReturn(ofNullable(DEVELOPER_ROLE()));
        when(membershipRepository.findByUserIdAndTeamId(expectedMembership.getUserId(),
                expectedMembership.getTeamId()))
                        .thenReturn(empty());
        when(membershipRepository
                .save(expectedMembership))
                        .thenReturn(expectedMembership);

        Membership actualMembership = membershipsService.assignRoleToMembership(expectedMembership);

        assertNotNull(actualMembership);
        assertEquals(actualMembership, expectedMembership);
        verify(roleRepository).findById(expectedMembership.getRole().getId());
    }

    @Test
    public void shouldFailToCreateMembershipWhenMembershipsIsNull() {
        assertThrows(NullPointerException.class,
                () -> membershipsService.assignRoleToMembership(null));
    }

    @Test
    public void shouldFailToCreateMembershipWhenTheUserDoesNotBelongToTheTeam() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        final Team team = mock(Team.class);
        when(team.getTeamMemberIds()).thenReturn(List.of(UUID.randomUUID()));
        when(teamsService.getTeam(expectedMembership.getTeamId())).thenReturn(team);
        assertThrows(InvalidMembershipException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));
    }

    @Test
    public void shouldFailToCreateMembershipWhenTeamIsNull() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(teamsService.getTeam(expectedMembership.getTeamId())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));
    }

    @Test
    public void shouldFailToCreateMembershipWhenItExists() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(teamsService.getTeam(expectedMembership.getTeamId())).thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(membershipRepository.findByUserIdAndTeamId(expectedMembership.getUserId(),
                expectedMembership.getTeamId()))
                        .thenReturn(of(expectedMembership));
        when(usersService.getUser(expectedMembership.getUserId())).thenReturn(GIANNI_USER());

        ResourceExistsException exception = assertThrows(ResourceExistsException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));

        assertEquals("Membership already exists", exception.getMessage());
        verify(roleRepository, times(0)).findById(any());
        verify(membershipRepository, times(0)).save(any());
    }

    @Test
    public void shouldFailToCreateMembershipWhenItHasInvalidRole() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setRole(null);

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));

        assertEquals("Invalid 'Role' object", exception.getMessage());
        verify(membershipRepository, times(0)).findByUserIdAndTeamId(any(), any());
        verify(roleRepository, times(0)).findById(any());
        verify(usersService, times(0)).getUser(any());
        verify(teamsService, times(0)).getTeam(any());
        verify(membershipRepository, times(0)).save(any());
    }

    @Test
    public void shouldGetMembershipById() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(membershipRepository.findById(UUID_1)).thenReturn(of(expectedMembership));
        final Membership membership = membershipsService.getMembership(UUID_1);

        assertEquals(expectedMembership, membership);
        verify(membershipRepository, times(1)).findById(UUID_1);
    }

    @Test
    public void shouldGetMembershipsByRoleId() {
        final Page<Membership> expectedMemberships = mock(Page.class);
        final Pageable pageable = mock(Pageable.class);
        when(membershipRepository.findByRoleId(UUID_1, pageable)).thenReturn(expectedMemberships);
        final Page<Membership> memberships = membershipsService.getMembershipsByRoleId(UUID_1, pageable);

        assertEquals(expectedMemberships, memberships);
        verify(membershipRepository, times(1)).findByRoleId(UUID_1, pageable);
    }

    @Test
    public void shouldFailToGetUnknownMembershipId() {
        when(membershipRepository.findById(UUID_1)).thenReturn(empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> membershipsService.getMembership(UUID_1));
        assertEquals("Membership 11111111-1111-1111-1111-111111111111 not found", exception.getMessage());
    }

    @Test
    public void shouldGetAllMemberships() {
        final Page<Membership> expectedMemberships = mock(Page.class);
        final Pageable pageable = mock(Pageable.class);

        when(membershipRepository.findAll(pageable)).thenReturn(expectedMemberships);
        final Page<Membership> memberships = membershipsService.getMemberships(pageable);

        assertEquals(expectedMemberships, memberships);
        verify(membershipRepository, times(1)).findAll(pageable);
    }

    @Test
    public void shouldFailToGetMembershipsWhenRoleIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> membershipsService.getMembershipsByRoleId(null, mock(Pageable.class)));
    }

}
