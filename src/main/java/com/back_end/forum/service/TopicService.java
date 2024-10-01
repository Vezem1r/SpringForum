package com.back_end.forum.service;

import com.back_end.forum.dto.SearchRequest;
import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.dto.TopicWithAttachmentsDto;
import com.back_end.forum.model.Attachment;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.model.Tag;
import com.back_end.forum.repository.AttachmentRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;

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
    public Optional<TopicWithAttachmentsDto> getTopicById(Long topicId) {
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);

        return optionalTopic.map(topic -> {
            List<Attachment> attachments = attachmentRepository.findByTopic_TopicId(topic.getTopicId());
            TopicWithAttachmentsDto topicDto = new TopicWithAttachmentsDto();

            topicDto.setTopicId(topic.getTopicId());
            topicDto.setTitle(topic.getTitle());
            topicDto.setContent(topic.getContent());
            topicDto.setCreatedAt(topic.getCreatedAt());
            topicDto.setUpdatedAt(topic.getUpdatedAt());
            topicDto.setUser(topic.getUser());
            topicDto.setRating(topic.getRating());
            topicDto.setCategory(topic.getCategory());
            topicDto.setTags(new ArrayList<>(topic.getTags()));
            topicDto.setAttachments(attachments);

            return topicDto;
        });
    }


    public List<Topic> getTopicsByCategory(Long categoryId) {
        List<Topic> topics = topicRepository.findByCategory_CategoryId(categoryId);
        topics.forEach(topic -> System.out.println("Topic: " + topic.getTitle()));
        return topics;
    }

    public List<Topic> searchTopics(SearchRequest searchRequest) {
        List<Topic> topics = topicRepository.findAll();
        if (searchRequest.getTitle() != null) {
            topics.removeIf(topic -> !topic.getTitle().toLowerCase().contains(searchRequest.getTitle().toLowerCase()));
        }

        if (searchRequest.getTags() != null && !searchRequest.getTags().isEmpty()) {
            topics.removeIf(topic ->
                    !topic.getTags().stream()
                            .map(Tag::getName)
                            .collect(Collectors.toSet())
                            .containsAll(searchRequest.getTags())
            );
        }

        if (searchRequest.getCategoryId() != null) {
            topics.removeIf(topic -> !topic.getCategory().getCategoryId().equals(searchRequest.getCategoryId()));
        }

        if (searchRequest.getMinRating() != null) {
            topics.removeIf(topic -> topic.getRating() < searchRequest.getMinRating());
        }

        if (searchRequest.getMaxRating() != null) {
            topics.removeIf(topic -> topic.getRating() > searchRequest.getMaxRating());
        }

        if (searchRequest.getUpdatedAfter() != null) {
            topics.removeIf(topic -> topic.getUpdatedAt().isBefore(searchRequest.getUpdatedAfter()));
        }

        if (searchRequest.getUpdatedBefore() != null) {
            topics.removeIf(topic -> topic.getUpdatedAt().isAfter(searchRequest.getUpdatedBefore()));
        }

        if ("rating".equals(searchRequest.getSortBy())) {
            topics.sort(Comparator.comparingInt(Topic::getRating));
        } else if ("createdAt".equals(searchRequest.getSortBy())) {
            topics.sort(Comparator.comparing(Topic::getCreatedAt));
        } else if ("updatedAt".equals(searchRequest.getSortBy())) {
            topics.sort(Comparator.comparing(Topic::getUpdatedAt));
        }

        if ("desc".equalsIgnoreCase(searchRequest.getSortOrder())) {
            Collections.reverse(topics);
        }

        return topics;
    }
}
