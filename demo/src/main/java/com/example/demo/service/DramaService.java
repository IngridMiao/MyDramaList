package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Drama;
import com.example.demo.repository.DramaRepository;

@Service
public class DramaService {

    @Autowired
    private DramaRepository dramaRepository;

    public List<Drama> getAllDramas() {
        return dramaRepository.findAll();
    }

    public Optional<Drama> getDramaById(String title) {
        return dramaRepository.findById(title);
    }

    public Drama saveDrama(Drama drama) {
        return dramaRepository.save(drama);
    }

    public void deleteDrama(String title) {
        dramaRepository.deleteById(title);
    }
}
