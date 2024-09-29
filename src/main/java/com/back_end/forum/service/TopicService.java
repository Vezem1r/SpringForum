package com.back_end.forum.service;

import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.back_end.forum.repository.CategoryRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;
    private final AttachmentService attachmentService;

    public Topic createTopic(TopicDto topicDTO, MultipartFile attachment) throws IOException {
        Topic topic = new Topic();
        topic.setTitle(topicDTO.getTitle());
        topic.setContent(topicDTO.getContent());
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());

        User user = userRepository.findById(topicDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        topic.setUser(user);

        topic.setCategory(categoryRepository.findById(topicDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));

        Set<Tag> tags = tagService.getOrCreateTags(topicDTO.getTagNames());
        topic.setTags(tags);

        Topic savedtopic = topicRepository.save(topic);
        System.out.println("Topic id" + savedtopic.getTopicId());

        if (attachment != null) {
            attachmentService.saveAttachment(attachment, savedtopic.getTopicId(), null);
        }

        return savedtopic;
    }

    public Page<Topic> getAllTopics(Pageable pageable) {
        return topicRepository.findAll(pageable);
    }
    public Optional<Topic> getTopicById(Long topicId) {
        return topicRepository.findById(topicId);
    }

    public List<Topic> getTopicsByCategory(Long categoryId) {
        List<Topic> topics = topicRepository.findByCategory_CategoryId(categoryId);
        topics.forEach(topic -> System.out.println("Topic: " + topic.getTitle()));
        return topics;
    }

    public List<Topic> searchTopics(Optional<Long> categoryId, Optional<String> title,
                                    Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate,
                                    Optional<String> sortBy, Optional<List<String>> tagNames) {

        if (categoryId.isPresent()) {
            return topicRepository.findByCategory_CategoryId(categoryId.get());
        }

        if (title.isPresent()) {
            return topicRepository.findByTitleContainingIgnoreCase(title.get());
        }

        if (startDate.isPresent() && endDate.isPresent()) {
            return topicRepository.findByCreatedAtBetween(startDate.get(), endDate.get());
        }

        if (tagNames.isPresent()) {
            List<Long> tagIds = tagService.getTagIdsByName(tagNames.get());
            return topicRepository.findByTags(tagIds, (long) tagIds.size());
        }

        if (sortBy.isPresent()) {
            switch (sortBy.get()) {
                case "createdAsc":
                    return topicRepository.findByOrderByCreatedAtAsc();
                case "createdDesc":
                    return topicRepository.findByOrderByCreatedAtDesc();
                case "updatedAsc":
                    return topicRepository.findByOrderByUpdatedAtAsc();
                case "updatedDesc":
                    return topicRepository.findByOrderByUpdatedAtDesc();
            }
        }

        return Collections.emptyList();
    }
}
