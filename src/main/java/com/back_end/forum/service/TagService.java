package com.back_end.forum.service;

import com.back_end.forum.dto.TagDto;
import com.back_end.forum.exception.BadRequest;
import com.back_end.forum.model.Tag;
import com.back_end.forum.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;

    public Tag createTag(TagDto tagDto){

        Optional<Tag> optionalTag = tagRepository.findByName(tagDto.getName());

        if (optionalTag.isPresent()) {
            log.warn("Attempted to create tag that already exists: {}", tagDto.getName());
            throw new BadRequest("Tag already exists");
        }
        Tag tag = new Tag();
        tag.setName(tagDto.getName());
        log.info("Created new tag: {}", tag.getName());
        return tagRepository.save(tag);
    }

    public Tag deleteTag(Long id){
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tag not found with id: {}", id);
                    return new BadRequest("Tag not found with id " + id);
                });
        tagRepository.delete(tag);
        log.info("Deleted tag: {}", tag.getName());
        return tag;
    }

    public Set<Tag> getOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName).orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(tagName);
                return tagRepository.save(newTag);
            });
            tags.add(tag);
        }
        log.info("Retrieved or created tags: {}", tags);
        return tags;
    }
}
