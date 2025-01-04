package org.ciaochat.service;

import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.UserDto;
import org.ciaochat.entity.User;
import org.ciaochat.mapper.UserMapper;
import org.ciaochat.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (isLoginTaken(userDto)) {
            throw new RuntimeException("such login already taken");
        }
        User userEntity = userMapper.toUser(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userMapper.toDto(userRepository.save(userEntity));
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean isLoginTaken(UserDto userDto) {
        return userRepository.existsByLogin(userDto.getLogin());
    }
}
