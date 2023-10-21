package com.example.officepcstore.utils;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.officepcstore.config.Constant.CART_TYPE;
import static com.example.officepcstore.config.Constant.REVIEW_GOOD_TYPE;

@Component
@Slf4j
@Getter
@Setter
public class RecommendProductUtils  implements Runnable {
    private UserRepository userRepository;
    private String type;
    private String catId;
    private String brandId;
    private String userId;

    @Override
    @Async
    @Transactional
    public void run() {
        log.info("Start add recommend score!");
        if (!type.isBlank() && !catId.isBlank() && !brandId.isBlank() && userId != null) {
            Optional<User> user = userRepository.findUserByIdAndStatusUser(userId, Constant.USER_ACTIVE);
            if (user.isPresent()) {
                try {
                    addScoreToUser(user.get(),catId);
                    addScoreToUser(user.get(),brandId);
                    userRepository.save(user.get());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    log.error("Failed to save recommendation score!");
                }
            }
        } else log.error("Invalid input data in Recommend check utils!");
        log.info("Add recommend score end!");
    }

    public void addScoreToUser (User user, String id) {
        int catScore = 1;
        if (user.getRecommendRating().containsKey(id)) {
            catScore = user.getRecommendRating().get(id);
            switch (type) {
                case REVIEW_GOOD_TYPE: catScore+=5 ;break;
                case CART_TYPE: catScore+=3 ;break;
                default: catScore+=1;
            }
        }
        user.getRecommendRating().put(id,
                catScore);
    }
}
