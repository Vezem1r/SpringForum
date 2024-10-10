package com.back_end.forum.service;

import com.back_end.forum.model.Message;
import com.back_end.forum.model.Notification;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.MessageRepository;
import com.back_end.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

//    public Message sendMessage(Long senderId, Long receiverId, String content) {
//        User sender = userRepository.findById(senderId)
//                .orElseThrow(() -> new RuntimeException("Sender not found"));
//        User receiver = userRepository.findById(receiverId)
//                .orElseThrow(() -> new RuntimeException("Receiver not found"));
//
//        Message message = new Message();
//        message.setSender(sender);
//        message.setReceiver(receiver);
//        message.setContent(content);
//        message.setTimestamp(LocalDateTime.now());
//
//        Notification notification = notificationService.createNotification(receiverId,
//                "You have new private message: " + content);
//
//        return messageRepository.save(message);
//    }
//
//    public List<Message> getMessages(Long userId1, Long userId2) {
//        User user1 = userRepository.findById(userId1)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        User user2 = userRepository.findById(userId2)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Message> messages = messageRepository.findBySenderAndReceiver(user1, user2);
//        messages.addAll(messageRepository.findByReceiverAndSender(user1, user2));
//        messages.sort(Comparator.comparing(Message::getTimestamp));
//
//        return messages;
//    }
}

