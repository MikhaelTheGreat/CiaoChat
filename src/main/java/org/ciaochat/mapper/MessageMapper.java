package org.ciaochat.mapper;

import org.ciaochat.dto.MessageDto;
import org.ciaochat.entity.Chat;
import org.ciaochat.entity.Message;
import org.ciaochat.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    @Mapping(source = "sender", target = "senderId", qualifiedByName = "senderToId")
    @Mapping(source = "chat", target = "chatId", qualifiedByName = "chatToId")
    @Mapping(source = "sender", target = "senderName", qualifiedByName = "senderToName")
    MessageDto toDto(Message message);

    Message toEntity(MessageDto messageDto);

    @Named("senderToId")
    default long senderToId(User sender) {
        return sender.getId();
    }

    @Named("chatToId")
    default long chatToId(Chat chat) {
        return chat.getId();
    }

    @Named("senderToName")
    default String senderToName(User sender) {
        return sender.getUsername();
    }
}
