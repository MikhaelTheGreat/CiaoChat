package org.ciaochat.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.ChatDto;
import org.ciaochat.dto.MessageDto;
import org.ciaochat.entity.Chat;
import org.ciaochat.mapper.ChatMapper;
import org.ciaochat.mapper.MessageMapper;
import org.ciaochat.repository.ChatRepository;
import org.ciaochat.repository.MessageRepository;
import org.ciaochat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final EntityManager entityManager;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final ChatMapper chatMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MessageDto> getChatMessages(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException());

        return new ArrayList<>(chat.getMessages().stream()
                .map(messageMapper::toDto)
                .toList());
    }

    @Transactional
    public ChatDto getChat(long chatId) {
        return chatRepository.findById(chatId)
                .map(chatMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException());
    }

    @Transactional
    public List<ChatDto> getUserAllChatsNames(Long userId) {
        return userService.getAllChats(userId);
    }


}
