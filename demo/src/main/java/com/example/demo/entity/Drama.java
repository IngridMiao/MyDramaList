package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "drama_list") // 根據你的習慣，這裡對應資料庫表名
@Data // 如果有安裝 Lombok 可以省去寫 Getter/Setter
@IdClass(DramaId.class)
public class Drama {
    @Id
    private String title;

    @Id
    private Long userId;

    private String actors;
    private String tag;
    private boolean shown = true;   //public or private, default true
    private Float grade;
    private String viewPoint; // 觀看點/心得
    private String link1;
    private String link2;
    private String link3;

    private String posterPath;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
