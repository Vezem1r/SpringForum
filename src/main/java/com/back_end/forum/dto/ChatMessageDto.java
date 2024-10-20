package com.back_end.forum.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private Long chatRoomId;
    private String senderUsername;
    private String recipientUsername;
    private String content;
}
