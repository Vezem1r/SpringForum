package com.back_end.forum.service;

import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.model.Tag;
import com.back_end.forum.model.Attachment;
import com.back_end.forum.repository.AttachmentRepository;
import com.back_end.forum.repository.CategoryRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final TagService tagService;

    public Topic createTopic(TopicDto topicDTO) {
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

        if (topicDTO.getAttachmentId() != null) {
            Attachment attachment = attachmentRepository.findById(topicDTO.getAttachmentId())
                    .orElseThrow(() -> new RuntimeException("Attachment not found"));
            topic.setAttachment(attachment);
        }

        Set<Tag> tags = tagService.getOrCreateTags(topicDTO.getTagNames());
        topic.setTags(tags);

        return topicRepository.save(topic);
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }
}
