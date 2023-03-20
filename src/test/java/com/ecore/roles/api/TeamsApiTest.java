package com.ecore.roles.api;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.utils.RestAssuredHelper;
import com.ecore.roles.web.dto.TeamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ecore.roles.utils.MockUtils.mockGetTeamById;
import static com.ecore.roles.utils.MockUtils.mockGetTeams;
import static com.ecore.roles.utils.RestAssuredHelper.getTeam;
import static com.ecore.roles.utils.RestAssuredHelper.getTeams;
import static com.ecore.roles.utils.TestData.ORDINARY_CORAL_LYNX_TEAM;
import static com.ecore.roles.utils.TestData.ORDINARY_CORAL_LYNX_TEAM_UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TeamsApiTest {

    private final RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @LocalServerPort
    private int port;

    @Autowired
    public TeamsApiTest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RestAssuredHelper.setUp(port);
    }

    @Test
    void shouldGetAllTeams() {
        final List<Team> expectedTeams = Arrays.asList(Team.builder().id(UUID.randomUUID()).build(),
                Team.builder().id(UUID.randomUUID()).build(), Team.builder().id(UUID.randomUUID()).build());
        mockGetTeams(mockServer, expectedTeams);

        final TeamDto[] actualTeams = getTeams().extract().as(TeamDto[].class);
        assertThat(actualTeams).hasSize(expectedTeams.size());
        assertThat(actualTeams[0].getId()).isEqualTo(expectedTeams.get(0).getId());
        assertThat(actualTeams[1].getId()).isEqualTo(expectedTeams.get(1).getId());
        assertThat(actualTeams[2].getId()).isEqualTo(expectedTeams.get(2).getId());
    }

    @Test
    void shouldGetTeamById() {
        final Team expectedTeam = ORDINARY_CORAL_LYNX_TEAM();
        mockGetTeamById(mockServer, ORDINARY_CORAL_LYNX_TEAM_UUID, expectedTeam);

        final TeamDto actualTeam = getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID).extract().as(TeamDto.class);
        assertThat(actualTeam.getId()).isEqualTo(expectedTeam.getId());
        assertThat(actualTeam.getName()).isEqualTo(expectedTeam.getName());
        assertThat(actualTeam.getTeamLeadId()).isEqualTo(expectedTeam.getTeamLeadId());
        assertThat(actualTeam.getTeamMemberIds()).isEqualTo(expectedTeam.getTeamMemberIds());
    }
}
