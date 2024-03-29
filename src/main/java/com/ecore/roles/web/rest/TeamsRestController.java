package com.ecore.roles.web.rest;

import com.ecore.roles.service.TeamsService;
import com.ecore.roles.web.TeamsApi;
import com.ecore.roles.web.dto.TeamDto;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecore.roles.web.dto.TeamDto.fromModel;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/teams")
public class TeamsRestController implements TeamsApi {

    private final TeamsService teamsService;

    @Override
    @GetMapping(
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "TeamsRestController.getTeams", description = "Time taken to execute 'get teams' request")
    public ResponseEntity<List<TeamDto>> getTeams() {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(teamsService.getTeams().stream()
                        .map(TeamDto::fromModel)
                        .collect(Collectors.toList()));
    }

    @Override
    @GetMapping(
            path = "/{teamId}",
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "TeamsRestController.getTeam(teamId)",
            description = "Time taken to execute 'get team by id' request")
    public ResponseEntity<TeamDto> getTeam(
            @PathVariable UUID teamId) {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(fromModel(teamsService.getTeam(teamId)));
    }

}
