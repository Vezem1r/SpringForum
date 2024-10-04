package com.back_end.forum.service;

import com.back_end.forum.dto.SearchRequest;
import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.dto.TopicWithAttachmentsDto;
import com.back_end.forum.model.*;
import com.back_end.forum.repository.*;
import com.back_end.forum.responses.AttachmentResponse;
import com.back_end.forum.responses.CommentResponse;
import com.back_end.forum.responses.TopicPageResponse;
import com.back_end.forum.responses.TopicResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final BannerService bannerService;
    private final CommentRepository commentRepository;

    public Topic createTopic(TopicDto topicDTO, String username) throws IOException {
            Topic topic = new Topic();
            topic.setTitle(topicDTO.getTitle());
            topic.setContent(topicDTO.getContent());
            topic.setCreatedAt(LocalDateTime.now());
            topic.setUpdatedAt(LocalDateTime.now());

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            topic.setUser(user);

            topic.setCategory(categoryRepository.findById(topicDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found")));

            Set<Tag> tags = tagService.getOrCreateTags(topicDTO.getTagNames());
            topic.setTags(tags);

            topic.setRating(0);

            Topic savedTopic = topicRepository.save(topic);

            if (topicDTO.getBanner() != null) {
                Banner banner = bannerService.saveBanner(topicDTO.getBanner());
                topic.setBanner(banner);
            }

            if (topicDTO.getAttachments() != null) {
                for (MultipartFile attachmentFile : topicDTO.getAttachments()) {
                    Attachment attachment = attachmentService.saveAttachment(attachmentFile);
                    topic.addAttachment(attachment);
                }
            }

        return topicRepository.save(savedTopic);
    }
    public Page<TopicResponseDto> getAllTopics(Pageable pageable) {
        Page<Topic> topicPage = topicRepository.findAll(pageable);

        return topicPage.map(topic -> new TopicResponseDto(
                topic.getId(),
                topic.getTitle(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getUser().getUsername(),
                topic.getCategory().getName(),
                topic.getTags().stream()
                        .map(tag -> tag.getName())
                        .collect(Collectors.toList()),
                topic.getRating()
        ));
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

    public TopicPageResponse getTopicPageById(Long topicId, Pageable pageable) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        String bannerUrl = topic.getBanner().getFilePath();

        List<AttachmentResponse> attachmentResponses = attachmentRepository.findByTopic_Id(topicId)
                .stream()
                .map(attachment -> new AttachmentResponse(
                        attachment.getId(),
                        attachment.getFilename(),
                        "/topicpage/attachments/download/" + attachment.getId()))
                .collect(Collectors.toList());


        Page<Comment> commentPage = commentRepository.findByTopic_IdAndParentCommentIsNull(topicId, pageable);
        List<CommentResponse> commentResponses = commentPage.getContent().stream()
                .map(comment -> new CommentResponse(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUser().getUsername(),
                        null,
                        commentRepository.countByParentComment_CommentId(comment.getCommentId()),
                        new ArrayList<>()
                ))
                .collect(Collectors.toList());

        return new TopicPageResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getUser().getUsername(),
                topic.getCategory().getName(),
                topic.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toList()),
                topic.getRating(),
                bannerUrl,
                attachmentResponses,
                commentResponses
        );
    }
}
