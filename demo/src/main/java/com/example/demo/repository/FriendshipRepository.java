package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUserIdAndStatus(Long userId, String status);
    List<Friendship> findByFriendIdAndStatus(Long friendId, String status);
    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);
}
