package com.back_end.forum.controller;

import com.back_end.forum.model.ChatMessage;
import com.back_end.forum.model.ChatRoom;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.ChatRoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/chatRoom")
public class ChatRoomController {
    private ChatRoomRepository chatRoomRepository;

    @GetMapping("/all")
    public List<ChatRoom> getChatRooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return chatRoomRepository.findBySenderOrRecipient(currentUser, null);
    }

    @GetMapping("/{chatRoomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();

        return chatRoom.getMessages();
    }
}
