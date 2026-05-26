package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Drama;
import com.example.demo.entity.DramaId;
import com.example.demo.repository.DramaRepository;
import com.example.demo.repository.UserRepository;

@Service
public class DramaService {

    @Autowired
    private DramaRepository dramaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private FriendshipService friendshipService;

    public List<Drama> getDramasByUserId(Long userId) {
        return dramaRepository.findByUserId(userId);
    }

    public List<Drama> getDramasByShown(Long userId, boolean shown) {
        return dramaRepository.findByUserIdAndShown(userId, shown);
    }

    public List<com.example.demo.dto.DramaResponse> getFriendsPublicDramas(Long userId) {
        List<com.example.demo.entity.User> friends = friendshipService.getFriends(userId);
        List<Long> friendIds = friends.stream().map(com.example.demo.entity.User::getId).collect(java.util.stream.Collectors.toList());
        
        List<Drama> dramas = dramaRepository.findByShown(true);
        return dramas.stream()
                .filter(drama -> friendIds.contains(drama.getUserId()))
                .map(drama -> {
                    String userName = friends.stream()
                            .filter(f -> f.getId().equals(drama.getUserId()))
                            .findFirst()
                            .map(com.example.demo.entity.User::getUserName)
                            .orElse("Unknown");
                    return new com.example.demo.dto.DramaResponse(
                            drama.getTitle(),
                            drama.getUserId(),
                            userName,
                            drama.getActors(),
                            drama.getTag(),
                            drama.isShown(),
                            drama.getGrade(),
                            drama.getViewPoint(),
                            drama.getLink1(),
                            drama.getLink2(),
                            drama.getLink3(),
                            drama.getPosterPath(),
                            drama.getCategory(),
                            drama.getUpdatedAt()
                    );
                }).collect(java.util.stream.Collectors.toList());
    }

    public List<com.example.demo.dto.DramaResponse> getAllPublicDramas() {
        List<Drama> dramas = dramaRepository.findByShown(true);
        return dramas.stream().map(drama -> {
            String userName = userRepository.findById(drama.getUserId())
                    .map(com.example.demo.entity.User::getUserName)
                    .orElse("Unknown");
            return new com.example.demo.dto.DramaResponse(
                    drama.getTitle(),
                    drama.getUserId(),
                    userName,
                    drama.getActors(),
                    drama.getTag(),
                    drama.isShown(),
                    drama.getGrade(),
                    drama.getViewPoint(),
                    drama.getLink1(),
                    drama.getLink2(),
                    drama.getLink3(),
                    drama.getPosterPath(),
                    drama.getCategory(),
                    drama.getUpdatedAt()
            );
        }).collect(java.util.stream.Collectors.toList());
    }

    public Optional<Drama> getDramaById(String title, Long userId) {
        return dramaRepository.findById(new DramaId(title, userId));
    }

    public Drama saveDrama(Drama drama) {
        tagService.updateTags(drama.getUserId(), drama.getTag());
        return dramaRepository.save(drama);
    }

    public void deleteDrama(String title, Long userId) {
        dramaRepository.deleteById(new DramaId(title, userId));
    }
}
