package com.back_end.forum.service;

import com.back_end.forum.dto.TagDto;
import com.back_end.forum.exception.BadRequest;
import com.back_end.forum.model.Tag;
import com.back_end.forum.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag createTag(TagDto tagDto){

        Optional<Tag> optionalTag = tagRepository.findByName(tagDto.getName());

        if(optionalTag.isPresent()) throw new BadRequest("Tag already exists");

        Tag tag = new Tag();
        tag.setName(tagDto.getName());
        return tagRepository.save(tag);
    }

    public Tag deleteTag(Long id){
        Tag category =  tagRepository.findById(id)
                .orElseThrow(() -> new BadRequest("Category not found with id " + id));
        tagRepository.delete(category);
        return category;
    }

}
