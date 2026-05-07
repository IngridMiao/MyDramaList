package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "drama_list") // 根據你的習慣，這裡對應資料庫表名
@Data // 如果有安裝 Lombok 可以省去寫 Getter/Setter
public class Drama {
    @Id
    private String title; // 你原本設定 video_name 為 PK
    private String tag;
    private Float grade;
    private String viewPoint; // 觀看點/心得
    private String link1;
    private String link2;
    private String link3;
}
