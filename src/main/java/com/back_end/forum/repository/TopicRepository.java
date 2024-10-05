package com.back_end.forum.repository;

import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor {
    int countByUser(User user);

    List<Topic> findAllByUser_Username(String username);

}
