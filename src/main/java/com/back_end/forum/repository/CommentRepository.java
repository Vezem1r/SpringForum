package com.back_end.forum.repository;

import com.back_end.forum.model.Comment;
import com.back_end.forum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    int countByUser(User user);

    Page<Comment> findByTopic_Id(Long topicId, Pageable pageable);

    Page<Comment> findByTopic_IdAndParentCommentIsNull(Long topicId, Pageable pageable);

    Page<Comment> findByParentComment_CommentId(Long parentId, Pageable pageable);

    int countByParentComment_CommentId(Long parentId);

    List<Comment> findByUser(User user);

    long countByTopic_IdAndParentCommentIsNull(Long topicId);
}
