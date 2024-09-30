package com.back_end.forum.repository;

import com.back_end.forum.model.Comment;
import com.back_end.forum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    int countByUser(User user);
}
