package com.back_end.forum.service;

import com.back_end.forum.Specifications.TopicSpecifications;
import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.*;
import com.back_end.forum.repository.*;
import com.back_end.forum.responses.AttachmentResponse;
import com.back_end.forum.responses.CommentResponse;
import com.back_end.forum.responses.TopicPageResponse;
import com.back_end.forum.responses.TopicResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });
            topic.setUser(user);

            topic.setCategory(categoryRepository.findById(topicDTO.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", topicDTO.getCategoryId());
                    return new RuntimeException("Category not found");
                }));
            Set<Tag> tags = tagService.getOrCreateTags(topicDTO.getTagNames());
            topic.setTags(tags);

            topic.setRating(0);

            Topic savedTopic = topicRepository.save(topic);
            log.info("Created new topic: {}", savedTopic.getTitle());

            if (topicDTO.getBanner() != null) {
                Banner banner = bannerService.saveBanner(topicDTO.getBanner());
                topic.setBanner(banner);
                log.info("Added banner to topic: {}", savedTopic.getTitle());
            }

            if (topicDTO.getAttachments() != null) {
                for (MultipartFile attachmentFile : topicDTO.getAttachments()) {
                    Attachment attachment = attachmentService.saveAttachment(attachmentFile);
                    attachmentRepository.save(attachment);
                    topic.addAttachment(attachment);
                    log.info("Added attachment to topic: {}, attachment filename: {}", savedTopic.getTitle(), attachment.getFilename());
                }
            }

        return topicRepository.save(savedTopic);
    }
    public Page<TopicResponseDto> getAllTopics(Pageable pageable) {
        Page<Topic> topicPage = topicRepository.findAll(pageable);
        log.info("Retrieved all topics, total count: {}", topicPage.getTotalElements());

        return topicPage.map(topic -> new TopicResponseDto(
                topic.getId(),
                topic.getTitle(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getUser().getUsername(),
                topic.getCategory().getName(),
                topic.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()),
                topic.getRating()
        ));
    }

    public Page<TopicResponseDto> searchTopics(String title, String category, List<String> tags,
                                               Integer minRating, Integer maxRating,
                                               String sortField, String sortDirection,
                                               Pageable pageable) {
        Specification<Topic> spec = Specification.where(TopicSpecifications.withTitle(title))
                .and(TopicSpecifications.withCategory(category))
                .and(TopicSpecifications.withTags(tags))
                .and(TopicSpecifications.withRatingBetween(minRating, maxRating));

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Topic> topicPage = topicRepository.findAll(spec, sortedPageable);
        log.info("Searched topics with title: {}, category: {}, tags: {}, total count: {}", title, category, tags, topicPage.getTotalElements());

        return topicPage.map(this::convertToDto);
    }


    public TopicResponseDto convertToDto(Topic topic){
        return new TopicResponseDto(
                topic.getId(),
                topic.getTitle(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getUser().getUsername(),
                topic.getCategory().getName(),
                topic.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                topic.getRating()
        );
    }

    public TopicPageResponse getTopicPageById(Long topicId, Pageable pageable) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.error("Topic not found with id: {}", topicId);
                    return new RuntimeException("Topic not found");
                });
        String bannerUrl = topic.getBanner() != null ? topic.getBanner().getFilePath() : null;

        List<AttachmentResponse> attachmentResponses = attachmentRepository.findByTopic_Id(topicId)
                .stream()
                .map(attachment -> new AttachmentResponse(
                        attachment.getId(),
                        attachment.getFilename(),
                        "/topicpage/attachments/download/" + attachment.getId()))
                .collect(Collectors.toList());


        Page<Comment> commentPage = commentRepository.findByTopic_IdAndParentCommentIsNull(topicId, pageable);
        List<CommentResponse> commentResponses = commentPage.getContent().stream()
                .map(comment -> {
                    List<AttachmentResponse> commentAttachments = comment.getAttachments().stream()
                            .map(attachment -> new AttachmentResponse(
                                    attachment.getId(),
                                    attachment.getFilename(),
                                    "/comment/attachments/download/" + attachment.getId()))
                            .collect(Collectors.toList());

                    return new CommentResponse(
                            comment.getCommentId(),
                            comment.getContent(),
                            comment.getCreatedAt(),
                            comment.getUser().getUsername(),
                            null,
                            comment.getRating(),
                            commentRepository.countByParentComment_CommentId(comment.getCommentId()),
                            new ArrayList<>(),
                            commentAttachments
                    );
                })
                .collect(Collectors.toList());

        long totalComments = commentRepository.countByTopic_IdAndParentCommentIsNull(topicId);
        int totalPages = (int) Math.ceil((double) totalComments / pageable.getPageSize());
        log.info("Retrieved topic page with id: {}, total comments: {}, total pages: {}", topicId, totalComments, totalPages);

        return new TopicPageResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getContent(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getUser().getUsername(),
                topic.getCategory().getName(),
                topic.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                topic.getRating(),
                bannerUrl,
                attachmentResponses,
                commentResponses,
                totalPages
        );
    }

    public Page<TopicResponseDto> getUserTopicsByUsername(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Topic> topics = topicRepository.findAllByUser_Username(username, pageable);
        log.info("Retrieved topics for user: {}, total count: {}", username, topics.getTotalElements());

        return topics.map(topic -> new TopicResponseDto(
                topic.getId(),
                topic.getTitle(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getUser().getUsername(),
                topic.getCategory().getName(),
                topic.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                topic.getRating()
        ));
    }
}
