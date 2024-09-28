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
            //Attachment savedAttachment = attachmentService.saveAttachment(attachment, savedtopic.getTopicId(), null);
            attachmentService.saveAttachment(attachment, savedtopic.getTopicId(), null);
            //savedtopic.setAttachment(savedAttachment);
        }

        return savedtopic;
    }

    public Page<Topic> getAllTopics(Pageable pageable) {
        return topicRepository.findAll(pageable);
    }
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }
}
