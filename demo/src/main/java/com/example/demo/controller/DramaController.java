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
    public ResponseEntity<List<Drama>> getAllDramas() {
        return ResponseEntity.ok(dramaService.getAllDramas());
    }

    @GetMapping("/{title}")
    public ResponseEntity<Drama> getDramaById(@PathVariable String title) {
        return dramaService.getDramaById(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Drama> createOrUpdateDrama(@RequestBody Drama drama) {
        return ResponseEntity.ok(dramaService.saveDrama(drama));
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteDrama(@PathVariable String title) {
        dramaService.deleteDrama(title);
        return ResponseEntity.noContent().build();
    }
}
