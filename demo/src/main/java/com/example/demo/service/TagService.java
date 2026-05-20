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

    public void updateTags(Long userId, String tagString) {
        if (tagString == null || tagString.isEmpty()) return;
        String[] tags = tagString.split(",");
        for (String tagName : tags) {
            String trimmed = tagName.trim();
            if (!trimmed.isEmpty()) {
                if (!tagRepository.existsByUserIdAndTagName(userId, trimmed)) {
                    Tag newTag = new Tag();
                    newTag.setUserId(userId);
                    newTag.setTagName(trimmed);
                    tagRepository.save(newTag);
                }
            }
        }
    }
}
