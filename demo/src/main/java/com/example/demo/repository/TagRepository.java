package com.example.demo.repository;

import com.example.demo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUserId(Long userId);
    boolean existsByUserIdAndTagName(Long userId, String tagName);
}
