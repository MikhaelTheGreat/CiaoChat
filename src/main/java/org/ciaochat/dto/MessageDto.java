package org.ciaochat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MessageDto {
    private Long id;
    private Long senderId;
    private Long chatId;
    private String content;
    private String senderName;
}
