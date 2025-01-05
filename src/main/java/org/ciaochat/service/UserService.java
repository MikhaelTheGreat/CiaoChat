package org.ciaochat.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.ChatDto;
import org.ciaochat.dto.UserDto;
import org.ciaochat.entity.User;
import org.ciaochat.mapper.ChatMapper;
import org.ciaochat.mapper.UserMapper;
import org.ciaochat.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ChatMapper chatMapper;

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
    public List<ChatDto> getAllChats(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));

        return user.getChats().stream()
                .map(chatMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean isLoginTaken(UserDto userDto) {
        return userRepository.existsByLogin(userDto.getLogin());
    }
}
