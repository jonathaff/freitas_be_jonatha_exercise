package com.ecore.roles.web.rest;

import com.ecore.roles.model.Membership;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.web.MembershipsApi;
import com.ecore.roles.web.dto.MembershipDto;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecore.roles.web.dto.MembershipDto.fromModel;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/memberships")
public class MembershipsRestController implements MembershipsApi {

    private final MembershipsService membershipsService;

    @Override
    @PostMapping(
            consumes = {APPLICATION_JSON_VALUE},
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "MembershipsRestController.assignRoleToMembership",
            description = "Time taken to execute 'assign role to membership' request")
    public ResponseEntity<MembershipDto> assignRoleToMembership(
            @NotNull @Valid @RequestBody MembershipDto membershipDto) {
        Membership membership = membershipsService.assignRoleToMembership(membershipDto.toModel());
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(fromModel(membership));
    }

    @GetMapping(
            path = "/{membershipId}",
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "MembershipsRestController.getMembership(membershipId)",
            description = "Time taken to execute 'get membership by id' request")
    public ResponseEntity<MembershipDto> getMembership(
            @PathVariable UUID membershipId) {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(MembershipDto.fromModel(membershipsService.getMembership(membershipId)));
    }

    @Override
    @GetMapping(
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "MembershipsRestController.getMemberships",
            description = "Time taken to execute 'get memberships' request")
    public ResponseEntity<List<MembershipDto>> getMemberships(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(membershipsService.getMemberships(pageable)
                        .stream()
                        .map(MembershipDto::fromModel)
                        .collect(Collectors.toList()));
    }

    @Override
    @GetMapping(
            path = "/search",
            produces = {APPLICATION_JSON_VALUE})
    @Timed(value = "MembershipsRestController.getMemberships(roleId)",
            description = "Time taken to execute 'get membership by roleId' request")
    public ResponseEntity<List<MembershipDto>> getMemberships(
            @RequestParam UUID roleId,
            Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(membershipsService.getMembershipsByRoleId(roleId, pageable)
                        .stream()
                        .map(MembershipDto::fromModel)
                        .collect(Collectors.toList()));
    }

}
