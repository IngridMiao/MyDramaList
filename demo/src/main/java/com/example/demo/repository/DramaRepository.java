package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Drama;

@Repository
public interface DramaRepository extends JpaRepository<Drama, String> {
    // 這裡繼承後，你就自動擁有 save(), findAll(), findById() 等功能了
}