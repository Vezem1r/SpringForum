package com.back_end.forum.repository;

import com.back_end.forum.model.ChatRoom;
import com.back_end.forum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findBySenderAndRecipient(User sender, User recipient);

    List<ChatRoom> findBySenderOrRecipient(User sender, User recipient);
}

