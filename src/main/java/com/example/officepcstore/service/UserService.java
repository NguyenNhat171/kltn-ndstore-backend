package com.example.officepcstore.service;

import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.UserMap;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.response.UserResponse;
import com.example.officepcstore.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMap userMapper;
    private final CloudinaryConfig cloudinary;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ResponseObjectData> findAll(String state, Pageable pageable) {
        Page<User> users;
        if (state.equalsIgnoreCase(Constant.USER_STATE_ACTIVATED) ||
                state.equalsIgnoreCase(Constant.USER_STATE_BLOCK) ||
                state.equalsIgnoreCase(Constant.USER_STATE_UNVERIFIED))
            users = userRepository.findAllByState(state.toLowerCase(), pageable);
        else users = userRepository.findAll(pageable);
        List<UserResponse> userResList = users.stream().map(userMapper::toUserRes).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", userResList);
        resp.put("totalQuantity", users.getTotalElements());
        resp.put("totalPage", users.getTotalPages());
        if (userResList.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all user success", resp));
        throw new NotFoundException("Can not found any user");
    }


//    public ResponseEntity<?> findUserById(String id) {
//        Optional<User> user = userRepository.findUserByIdAndState(id, Constant.USER_STATE_ACTIVATED);
//        if (user.isPresent()) {
//            UserResponse res = userMapper.toUserRes(user.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Get user success", res));
//        }
//        throw new NotFoundException("Can not found user with id " + id );
//    }

}
