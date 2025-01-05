package org.ciaochat.mapper;

import org.ciaochat.dto.ChatDto;
import org.ciaochat.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMapper {
    ChatDto toDto(Chat chat);
}
