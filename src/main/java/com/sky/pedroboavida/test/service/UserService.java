package com.sky.pedroboavida.test.service;

import com.sky.pedroboavida.test.converter.UserToUserDTOConverter;
import com.sky.pedroboavida.test.model.CreateUserRequest;
import com.sky.pedroboavida.test.model.UpdateUserRequest;
import com.sky.pedroboavida.test.model.UserDTO;
import com.sky.pedroboavida.test.entity.User;
import com.sky.pedroboavida.test.exception.UserAlreadyExistsException;
import com.sky.pedroboavida.test.exception.UserNotFoundException;
import com.sky.pedroboavida.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserToUserDTOConverter userToUserDTOConverter;

    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail(), "Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());

        User savedUser = userRepository.save(user);
        return userToUserDTOConverter.convert(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userToUserDTOConverter.convert(user);
    }

    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException(request.getEmail(), "Email already registered");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        User updatedUser = userRepository.save(user);
        return userToUserDTOConverter.convert(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}

