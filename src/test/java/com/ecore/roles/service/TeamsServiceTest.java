package com.ecore.roles.service;

import com.ecore.roles.client.TeamsClient;
import com.ecore.roles.client.model.Team;
import com.ecore.roles.service.impl.TeamsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.ecore.roles.utils.TestData.ORDINARY_CORAL_LYNX_TEAM;
import static com.ecore.roles.utils.TestData.ORDINARY_CORAL_LYNX_TEAM_UUID;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamsServiceTest {

    @InjectMocks
    private TeamsServiceImpl teamsService;
    @Mock
    private TeamsClient teamsClient;

    @Test
    void shouldGetTeamWhenTeamIdExists() {
        Team expectedTeam = ORDINARY_CORAL_LYNX_TEAM();
        when(teamsClient.getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(expectedTeam));
        assertEquals(expectedTeam, teamsService.getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID));
    }

    @Test
    void shouldListAllTeams() {
        final List<Team> expectedTeams = of(ORDINARY_CORAL_LYNX_TEAM());
        when(teamsClient.getTeams())
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(expectedTeams));
        assertEquals(expectedTeams, teamsService.getTeams());
    }
}
