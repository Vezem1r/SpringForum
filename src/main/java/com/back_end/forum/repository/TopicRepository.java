package com.back_end.forum.repository;

import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByCategory_CategoryId(Long categoryId);

    List<Topic> findByTitleContainingIgnoreCase(String title);

    List<Topic> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Topic> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Topic t JOIN t.tags tag WHERE tag.tagId IN :tagIds GROUP BY t HAVING COUNT(tag.tagId) = :tagCount")
    List<Topic> findByTags(@Param("tagIds") List<Long> tagIds, @Param("tagCount") Long tagCount);

    List<Topic> findByOrderByCreatedAtAsc();
    List<Topic> findByOrderByCreatedAtDesc();
    List<Topic> findByOrderByUpdatedAtAsc();
    List<Topic> findByOrderByUpdatedAtDesc();

    int countByUser(User user);
}
