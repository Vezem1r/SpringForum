package com.back_end.forum.controller;

import com.back_end.forum.dto.ChatMessageDto;
import com.back_end.forum.model.ChatMessage;
import com.back_end.forum.model.ChatRoom;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.ChatMessageRepository;
import com.back_end.forum.repository.ChatRoomRepository;
import com.back_end.forum.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@Slf4j
@AllArgsConstructor
public class WebsocketController {
    private SimpMessagingTemplate messagingTemplate;
    private ChatRoomRepository chatRoomRepository;
    private ChatMessageRepository chatMessageRepository;
    private UserRepository userRepository;

    @MessageMapping("/chat")
    public void chat(ChatMessageDto message) {
        log.info("Message accepted");

        if (message.getChatRoomId() < 0) {
            log.warn("Chat room id cannot be negative");
            return;
        }

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(message.getChatRoomId());
        Optional<User> sender = userRepository.findByUsername(message.getSenderUsername());
        Optional<User> recipient = userRepository.findByUsername(message.getRecipientUsername());
        ChatRoom room;

        if (sender.isEmpty()) {
            log.warn("Sender not found: {}", message.getSenderUsername());
            return;
        }
        if (recipient.isEmpty()) {
            log.warn("Recipient not found: {}", message.getRecipientUsername());
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender.get());
        chatMessage.setContent(message.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());

        if (chatRoom.isPresent()) {
            room = chatRoom.get();
        } else {
            room = new ChatRoom();
            room.setSender(sender.get());
            room.setRecipient(recipient.get());
            room.setMessages(new ArrayList<>());
            room = chatRoomRepository.save(room);
        }

        chatMessage.setChatRoom(room);
        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/message/" + message.getChatRoomId(), chatMessage);
    }
}