package org.ciaochat.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.ChatDto;
import org.ciaochat.dto.MessageDto;
import org.ciaochat.entity.Chat;
import org.ciaochat.entity.Message;
import org.ciaochat.entity.User;
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
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MessageDto> getChatMessages(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException());

        return chat.getMessages().stream()
                .map(messageMapper::toDto)
                .toList();
    }

    @Transactional
    public void sendMessage(MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);
        message.setSender(entityManager.getReference(User.class, messageDto.getSenderId()));
        message.setChat(entityManager.getReference(Chat.class, messageDto.getChatId()));
        messageRepository.save(message);
    }

    @Transactional
    public List<ChatDto> getUserAllChatsNames(Long userId) {
        return userService.getAllChats(userId);
    }

    @Transactional
    public ChatDto createChat(ChatDto chatDto) {
        Chat chat = new Chat();
        chat.setName(chatDto.getName());

        List<User> users = new ArrayList<>();
        chatDto.getUsersId().forEach(id -> {
            if(!userRepository.existsById(id)) {
                throw new EntityNotFoundException();
            }
        });
        chatDto.getUsersId().forEach(id -> {
            User user = entityManager.getReference(User.class, id);
            users.add(user);
        });

        chat.setUsers(users);
        chat = chatRepository.save(chat);

        chatDto.setId(chat.getId());
        return chatDto;
    }

}
