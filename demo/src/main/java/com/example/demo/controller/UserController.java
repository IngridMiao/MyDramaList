package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 允許所有來源的前端連線
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private com.example.demo.service.FriendshipService friendshipService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/{id}/friends")
    public ResponseEntity<?> addFriend(@PathVariable Long id, @RequestBody com.example.demo.dto.FriendRequest request) {
        String result = friendshipService.addFriend(id, request.getFriendUserName());
        if ("OK".equals(result)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable Long id) {
        return ResponseEntity.ok(friendshipService.getFriends(id));
    }

    @GetMapping("/{id}/friend-requests")
    public ResponseEntity<List<User>> getPendingRequests(@PathVariable Long id) {
        return ResponseEntity.ok(friendshipService.getPendingRequests(id));
    }

    @PostMapping("/{id}/friend-requests/{requesterId}/accept")
    public ResponseEntity<?> acceptFriend(@PathVariable Long id, @PathVariable Long requesterId) {
        String result = friendshipService.acceptFriend(id, requesterId);
        if ("OK".equals(result)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{id}/friend-requests/{requesterId}/decline")
    public ResponseEntity<?> declineFriend(@PathVariable Long id, @PathVariable Long requesterId) {
        String result = friendshipService.declineFriend(id, requesterId);
        if ("OK".equals(result)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdateUser(@RequestBody User user) {
        System.out.println("Registering user: " + user.getUserName());
        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            return ResponseEntity.badRequest().body("帳號不能為空");
        }
        // 檢查帳號是否已存在
        if (userService.getUserByUserName(user.getUserName()).isPresent()) {
            return ResponseEntity.status(409).body("帳號已存在");
        }
        User savedUser = userService.saveUser(user);
        System.out.println("User saved with ID: " + savedUser.getId());
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("Login attempt: " + user.getUserName());
        return userService.getUserByUserName(user.getUserName())
                .map(u -> {
                    if (u.getPassword() != null && userService.verifyPassword(user.getPassword(), u.getPassword())) {
                        System.out.println("Login successful: " + user.getUserName());
                        return ResponseEntity.ok(u);
                    } else {
                        System.out.println("Login failed: Incorrect password for " + user.getUserName());
                        return ResponseEntity.status(401).body("密碼錯誤");
                    }
                })
                .orElseGet(() -> {
                    System.out.println("Login failed: User not found: " + user.getUserName());
                    return ResponseEntity.status(404).body("查無此帳號");
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
