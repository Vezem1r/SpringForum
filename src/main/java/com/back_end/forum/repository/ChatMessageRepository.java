package com.back_end.forum.repository;

import com.back_end.forum.model.ChatMessage;
import com.back_end.forum.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

}
