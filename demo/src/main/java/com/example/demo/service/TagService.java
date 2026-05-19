package com.example.demo.service;

import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getTagsByUserId(Long userId) {
        return tagRepository.findByUserId(userId);
    }

    public Tag saveTag(Tag tag) {
        if (tagRepository.existsByUserIdAndTagName(tag.getUserId(), tag.getTagName())) {
            return tag; // Or handle as conflict, but for now just return existing
        }
        return tagRepository.save(tag);
    }
}
