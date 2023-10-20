package com.ecore.roles.api;

import com.ecore.roles.client.model.User;
import com.ecore.roles.utils.RestAssuredHelper;
import com.ecore.roles.web.dto.UserDto;
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

import static com.ecore.roles.utils.MockUtils.mockGetUserById;
import static com.ecore.roles.utils.MockUtils.mockGetUsers;
import static com.ecore.roles.utils.RestAssuredHelper.getUser;
import static com.ecore.roles.utils.RestAssuredHelper.getUsers;
import static com.ecore.roles.utils.TestData.GIANNI_USER;
import static com.ecore.roles.utils.TestData.GIANNI_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersApiTest {

    private final RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @LocalServerPort
    private int port;

    @Autowired
    public UsersApiTest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RestAssuredHelper.setUp(port);
    }

    @Test
    void shouldGetAllUsers() {
        final List<User> expectedUsers = Arrays.asList(User.builder().id(UUID.randomUUID()).build(),
                User.builder().id(UUID.randomUUID()).build(), User.builder().id(UUID.randomUUID()).build());
        mockGetUsers(mockServer, expectedUsers);

        final UserDto[] actualUsers = getUsers().extract().as(UserDto[].class);
        assertThat(actualUsers).hasSize(expectedUsers.size());
        assertThat(actualUsers[0].getId()).isEqualTo(expectedUsers.get(0).getId());
        assertThat(actualUsers[1].getId()).isEqualTo(expectedUsers.get(1).getId());
        assertThat(actualUsers[2].getId()).isEqualTo(expectedUsers.get(2).getId());
    }

    @Test
    void shouldGetUserById() {
        final User expectedUser = GIANNI_USER();
        mockGetUserById(mockServer, GIANNI_USER_UUID, expectedUser);

        final UserDto actualUser = getUser(GIANNI_USER_UUID).extract().as(UserDto.class);
        assertThat(actualUser.getId()).isEqualTo(expectedUser.getId());
        assertThat(actualUser.getLocation()).isEqualTo(expectedUser.getLocation());
        assertThat(actualUser.getFirstName()).isEqualTo(expectedUser.getFirstName());
        assertThat(actualUser.getLastName()).isEqualTo(expectedUser.getLastName());
        assertThat(actualUser.getDisplayName()).isEqualTo(expectedUser.getDisplayName());
        assertThat(actualUser.getAvatarUrl()).isEqualTo(expectedUser.getAvatarUrl());
    }
}
