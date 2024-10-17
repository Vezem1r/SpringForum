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
        log.info("Fetching admin page data. Start of today: {}", todayStart);

        long totalUsers = userRepository.count();
        log.debug("Total users count: {}", totalUsers);
        long loggedInTodayUsers = userRepository.countUsersByLastLogin(todayStart);
        log.debug("Total users logged in today: {}", loggedInTodayUsers);

        long totalTopics = topicRepository.count();
        log.debug("Total topics count: {}", totalTopics);
        long topicsCreatedToday = topicRepository.countTopicsByCreatedAt(todayStart);
        log.debug("Total topics created today: {}", topicsCreatedToday);

        long totalComments = commentRepository.count();
        log.debug("Total comments count: {}", topicsCreatedToday);
        long commentsCreatedToday = commentRepository.countCommentsByCreatedAt(todayStart);
        log.debug("Total comments created today: {}", topicsCreatedToday);

        return new AdminPageDto(totalUsers, loggedInTodayUsers, totalTopics, topicsCreatedToday, totalComments, commentsCreatedToday);
    }

    private LocalDateTime getStartOfToday() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        log.debug("Start of today calculated: {}", todayStart);
        return todayStart;
    }
}
