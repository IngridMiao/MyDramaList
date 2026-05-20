package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Drama;
import com.example.demo.entity.DramaId;

@Repository
public interface DramaRepository extends JpaRepository<Drama, DramaId> {
    // 這裡繼承後，你就自動擁有 save(), findAll(), findById() 等功能了
    List<Drama> findByUserId(Long userId);
    List<Drama> findByUserIdAndShown(Long userId, boolean shown);
    List<Drama> findByShown(boolean shown);
}