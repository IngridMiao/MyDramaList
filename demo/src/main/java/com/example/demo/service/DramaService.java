package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Drama;
import com.example.demo.entity.DramaId;
import com.example.demo.repository.DramaRepository;

@Service
public class DramaService {

    @Autowired
    private DramaRepository dramaRepository;

    public List<Drama> getDramasByUserId(Long userId) {
        return dramaRepository.findByUserId(userId);
    }

    public List<Drama> getDramasByShown(Long userId, boolean shown) {
        return dramaRepository.findByUserIdAndShown(userId, shown);
    }

    public Optional<Drama> getDramaById(String title, Long userId) {
        return dramaRepository.findById(new DramaId(title, userId));
    }

    public Drama saveDrama(Drama drama) {
        return dramaRepository.save(drama);
    }

    public void deleteDrama(String title, Long userId) {
        dramaRepository.deleteById(new DramaId(title, userId));
    }
}
