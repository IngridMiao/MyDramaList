package com.example.demo.controller;

import com.example.demo.entity.Drama;
import com.example.demo.service.DramaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dramas")
@CrossOrigin(origins = "*") // 允許所有來源的前端連線 (開發階段設為 "*" 比較方便)
public class DramaController {

    @Autowired
    private DramaService dramaService;

    @GetMapping
    public ResponseEntity<List<Drama>> getAllDramas(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean shown) {
        if (shown != null) {
            return ResponseEntity.ok(dramaService.getDramasByShown(userId, shown));
        }
        return ResponseEntity.ok(dramaService.getDramasByUserId(userId));
    }

    @GetMapping("/{title}")
    public ResponseEntity<Drama> getDramaById(
            @PathVariable String title,
            @RequestParam Long userId) {
        return dramaService.getDramaById(title, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Drama> createOrUpdateDrama(@RequestBody Drama drama) {
        return ResponseEntity.ok(dramaService.saveDrama(drama));
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteDrama(
            @PathVariable String title,
            @RequestParam Long userId) {
        dramaService.deleteDrama(title, userId);
        return ResponseEntity.noContent().build();
    }
}
