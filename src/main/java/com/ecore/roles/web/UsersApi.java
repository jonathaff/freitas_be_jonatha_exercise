package com.ecore.roles.web;

import com.ecore.roles.web.dto.UserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface UsersApi {

    ResponseEntity<List<UserDto>> getUsers(Pageable pageable);

    ResponseEntity<UserDto> getUser(UUID userId);
}
