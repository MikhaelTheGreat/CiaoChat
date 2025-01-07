package org.ciaochat.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
import org.ciaochat.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebsocketListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final EntityManager entityManager;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

//    @MessageMapping("/server/{userId}/create_chat")
//    public void createChat(@Payload String message, @DestinationVariable String userId) {
//
//    }
//
//    @MessageMapping("/server/{userId}/delete_chat")
//    public void deleteChat(@Payload String message, @DestinationVariable String userId) {
//
//    }
//
//    @MessageMapping("/server/{userId}/send_message")
//    public void sendMessage(@Payload String message, @DestinationVariable String userId) {
//
//    }

    @MessageMapping("/server/create_chat")
    public void createChat(@Payload String message) {
        ChatDto chatDto = null;
        try {
            objectMapper.readValue(message, ChatDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

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
            messagingTemplate.convertAndSend("/client/" + userId, chatDto);
        });
    }

    @MessageMapping("/server/delete_chat")
    public void deleteChat(@Payload String message) {
        Long chatId;
        try {
            chatId = objectMapper.readValue(message, IdDto.class)
                    .getId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        chatRepository.deleteChatUserByChatId(chatId);
        chatRepository.deleteById(chatId);

        getChatParticipantsIds(chatId).forEach(userId -> {
            messagingTemplate.convertAndSend("/client/" + userId, chatId);
        });
    }

    @MessageMapping("/server/send_message")
    public void sendMessage(@Payload String jsonMessage) {
        MessageDto messageDto;
        try {
            messageDto = objectMapper.readValue(jsonMessage, MessageDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Message message = messageMapper.toEntity(messageDto);
        message.setSender(entityManager.getReference(User.class, messageDto.getSenderId()));
        message.setChat(entityManager.getReference(Chat.class, messageDto.getChatId()));
        messageRepository.save(message);

        List<Long> chatParticipantsIds = getChatParticipantsIds(messageDto.getChatId());
        chatParticipantsIds.forEach(userIds -> {
            messagingTemplate.convertAndSend("/client/" + userIds, messageDto);
        });
    }

    private List<Long> getChatParticipantsIds(Long chatId) {
        return chatRepository.getChatParticipantsByChatId(chatId);
    }
}
