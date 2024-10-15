package com.back_end.forum.service.admin;

import com.back_end.forum.dto.AdminPageDto;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class AdminService {

    private UserRepository userRepository;
    private TopicRepository topicRepository;
    private CommentRepository commentRepository;

    public AdminPageDto getAdminPage() {
        LocalDateTime todayStart = getStartOfToday();

        long totalUsers = userRepository.count();
        long loggedInTodayUsers = userRepository.countUserByLastLogin(todayStart);

        long totalTopics = topicRepository.count();
        long topicsCreatedToday = topicRepository.countTopicsByCreatedAt(todayStart);

        long totalComments = commentRepository.count();
        long commentsCreatedToday = commentRepository.countCommentsByCreatedAt(todayStart);

        return new AdminPageDto(totalUsers, loggedInTodayUsers, totalTopics, topicsCreatedToday, totalComments, commentsCreatedToday);
    }

    private LocalDateTime getStartOfToday() {
        return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
