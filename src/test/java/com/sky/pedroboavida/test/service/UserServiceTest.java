package com.sky.pedroboavida.test.service;

import com.sky.pedroboavida.test.converter.UserToUserDTOConverter;
import com.sky.pedroboavida.test.model.CreateUserRequest;
import com.sky.pedroboavida.test.model.UpdateUserRequest;
import com.sky.pedroboavida.test.model.UserDTO;
import com.sky.pedroboavida.test.entity.User;
import com.sky.pedroboavida.test.exception.UserAlreadyExistsException;
import com.sky.pedroboavida.test.exception.UserNotFoundException;
import com.sky.pedroboavida.test.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserToUserDTOConverter userToUserDTOConverter;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setName("Test User");

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setName("Test User");

        createRequest = new CreateUserRequest();
        createRequest.setEmail("test@example.com");
        createRequest.setPassword("password123");
        createRequest.setName("Test User");
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userToUserDTOConverter.convert(any(User.class))).thenReturn(testUserDTO);

        UserDTO result = userService.createUser(createRequest);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(passwordEncoder).encode(createRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userToUserDTOConverter).convert(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userToUserDTOConverter.convert(any(User.class))).thenReturn(testUserDTO);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
        verify(userToUserDTOConverter).convert(any(User.class));
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_Success() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@example.com");

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(1L);
        updatedUserDTO.setEmail("updated@example.com");
        updatedUserDTO.setName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userToUserDTOConverter.convert(any(User.class))).thenReturn(updatedUserDTO);

        UserDTO result = userService.updateUser(1L, updateRequest);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
        verify(userToUserDTOConverter).convert(any(User.class));
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updateRequest));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(any());
    }
}

