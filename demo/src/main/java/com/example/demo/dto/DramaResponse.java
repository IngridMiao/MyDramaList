package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DramaResponse {
    private String title;
    private Long userId;
    private String userName;
    private String actors;
    private String tag;
    private boolean shown;
    private Float grade;
    private String viewPoint;
    private String link1;
    private String link2;
    private String link3;
    private String posterPath;
    private String category;
    private LocalDateTime updatedAt;
}
