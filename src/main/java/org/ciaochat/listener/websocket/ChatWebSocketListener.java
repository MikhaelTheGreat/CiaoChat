package org.ciaochat.listener.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ciaochat.dto.ChatDto;
import org.ciaochat.dto.IdDto;
import org.ciaochat.dto.MessageDto;
import org.ciaochat.entity.Chat;
import org.ciaochat.entity.Message;
import org.ciaochat.entity.User;
import org.ciaochat.mapper.MessageMapper;
import org.ciaochat.repository.ChatRepository;
import org.ciaochat.repository.MessageRepository;
import org.ciaochat.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final EntityManager entityManager;
    private final MessageMapper messageMapper;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    @MessageMapping("/create_chat")
    public void createChat(@Payload ChatDto chatDto) {
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

        getChatParticipantsIds(chatDto.getId()).forEach(userId -> {
            messagingTemplate.convertAndSend("/client/" + userId + "/create_chat", chatDto);
        });
    }

    @Transactional
    @MessageMapping("/delete_chat")
    public void deleteChat(@Payload IdDto idDto) {
        Long chatId = idDto.getId();
        getChatParticipantsIds(chatId).forEach(userId -> {
            messagingTemplate.convertAndSend("/client/" + userId + "/delete_chat", chatId);
        });

        chatRepository.deleteChatUserByChatId(chatId);
        chatRepository.deleteById(chatId);
    }

    @Transactional
    @MessageMapping("/send_message")
    public void sendMessage(@Payload MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);
        message.setSender(entityManager.getReference(User.class, messageDto.getSenderId()));
        message.setChat(entityManager.getReference(Chat.class, messageDto.getChatId()));
        messageRepository.save(message);

        List<Long> chatParticipantsIds = getChatParticipantsIds(messageDto.getChatId());
        chatParticipantsIds.forEach(userIds -> {
            messagingTemplate.convertAndSend("/client/" + userIds + "/send_message", messageDto);
        });
    }

    private List<Long> getChatParticipantsIds(Long chatId) {
        return chatRepository.getChatParticipantsByChatId(chatId);
    }
}
