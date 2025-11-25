package com.sky.pedroboavida.test.controller;

import com.sky.pedroboavida.test.api.UsersApi;
import com.sky.pedroboavida.test.model.CreateUserRequest;
import com.sky.pedroboavida.test.model.UpdateUserRequest;
import com.sky.pedroboavida.test.model.UserDTO;
import com.sky.pedroboavida.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserDTO> createUser(CreateUserRequest createUserRequest) {
        UserDTO user = userService.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    public ResponseEntity<UserDTO> getUserById(Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(Long id, UpdateUserRequest updateUserRequest) {
        UserDTO user = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

