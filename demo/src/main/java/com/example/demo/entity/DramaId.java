package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DramaId implements Serializable {
    private String title;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DramaId dramaId = (DramaId) o;
        return Objects.equals(title, dramaId.title) && Objects.equals(userId, dramaId.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, userId);
    }
}
