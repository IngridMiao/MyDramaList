package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Friendship;
import com.example.demo.entity.User;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    public String addFriend(Long userId, String friendUserName) {
        Optional<User> friendOpt = userRepository.findByUserName(friendUserName);
        if (friendOpt.isEmpty()) {
            return "查無此用戶";
        }

        User friend = friendOpt.get();
        if (friend.getId().equals(userId)) {
            return "不能加自己為好友";
        }

        Optional<Friendship> existing = friendshipRepository.findByUserIdAndFriendId(userId, friend.getId());
        if (existing.isPresent()) {
            if ("ACCEPTED".equals(existing.get().getStatus())) {
                return "已經是好友了";
            } else {
                return "已發送過申請，等待對方確認";
            }
        }

        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friend.getId());
        friendship.setStatus("PENDING");
        friendshipRepository.save(friendship);

        return "OK";
    }

    public List<User> getFriends(Long userId) {
        List<Friendship> friendships = friendshipRepository.findByUserIdAndStatus(userId, "ACCEPTED");
        return friendships.stream()
                .map(f -> userRepository.findById(f.getFriendId()).orElse(null))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }

    public List<User> getPendingRequests(Long userId) {
        List<Friendship> requests = friendshipRepository.findByFriendIdAndStatus(userId, "PENDING");
        return requests.stream()
                .map(f -> userRepository.findById(f.getUserId()).orElse(null))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }

    public String acceptFriend(Long userId, Long requesterId) {
        Optional<Friendship> requestOpt = friendshipRepository.findByUserIdAndFriendId(requesterId, userId);
        if (requestOpt.isEmpty()) {
            return "找不到該好友申請";
        }

        Friendship request = requestOpt.get();
        request.setStatus("ACCEPTED");
        friendshipRepository.save(request);

        // Make it mutual
        Optional<Friendship> mutualOpt = friendshipRepository.findByUserIdAndFriendId(userId, requesterId);
        if (mutualOpt.isEmpty()) {
            Friendship mutual = new Friendship();
            mutual.setUserId(userId);
            mutual.setFriendId(requesterId);
            mutual.setStatus("ACCEPTED");
            friendshipRepository.save(mutual);
        } else {
            Friendship mutual = mutualOpt.get();
            mutual.setStatus("ACCEPTED");
            friendshipRepository.save(mutual);
        }

        return "OK";
    }

    public String declineFriend(Long userId, Long requesterId) {
        Optional<Friendship> requestOpt = friendshipRepository.findByUserIdAndFriendId(requesterId, userId);
        if (requestOpt.isPresent()) {
            friendshipRepository.delete(requestOpt.get());
        }
        return "OK";
    }
}
