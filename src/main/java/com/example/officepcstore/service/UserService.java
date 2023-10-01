package com.example.officepcstore.service;

import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.UserMap;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enums.EnumSocial;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.ChangePassReq;
import com.example.officepcstore.payload.request.RegisterReq;
import com.example.officepcstore.payload.request.ResetForgetPassReq;
import com.example.officepcstore.payload.request.UserReq;
import com.example.officepcstore.payload.response.UserResponse;
import com.example.officepcstore.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMap userMap;
    private final CloudinaryConfig cloudinary;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ResponseObjectData> findAll(String state, Pageable pageable) {
        Page<User> users;
        if (state.equalsIgnoreCase(Constant.USER_ACTIVE) ||
                state.equalsIgnoreCase(Constant.USER_BLOCK) ||
                state.equalsIgnoreCase(Constant.USER_UNVERIFIED))
            users = userRepository.findAllByState(state, pageable);
        else users = userRepository.findAll(pageable);
        List<UserResponse> userResList = users.stream().map(userMap::toUserRes).collect(Collectors.toList());
        Map<String, Object> userresp = new HashMap<>();
        userresp.put("allPage", users.getTotalPages());
        userresp.put("allQuantity", users.getTotalElements());
        userresp.put("list", userResList);
        if (userResList.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all user success", userresp));
        throw new NotFoundException("Can not found any user");
    }


    public ResponseEntity<?> findUserById(String id) {
        Optional<User> user = userRepository.findUserByIdAndState(id, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            UserResponse res = userMap.toUserRes(user.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get user success", res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found user", ""));
    }

    public ResponseEntity<?> checkUserByAdmin(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UserResponse res = userMap.toUserRes(user.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get user success", res));
        }
       return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found user", ""));
    }


    @Transactional
    public ResponseEntity<?> addAccount(RegisterReq req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new AppException(HttpStatus.CONFLICT.value(), "Email exists");
        req.setPassword(passwordEncoder.encode(req.getPassword()));
        User user = userMap.toUser(req);

        user.setState(Constant.USER_ACTIVE);
        try {
            userRepository.insert(user);
        } catch (Exception e) {
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObjectData(true, "Add admin successfully ", "")
        );
    }

    public ResponseEntity<?> updateUserAvatar(String id, MultipartFile file) {
        Optional<User> user = userRepository.findUserByIdAndState(id, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            if (file != null && !file.isEmpty()) {
                try {
                    String imgUrl = cloudinary.uploadImage(file, user.get().getAvatar());
                    user.get().setAvatar(imgUrl);
                    userRepository.save(user.get());
                } catch (IOException e) {
                    throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload image");
                }
            }
            UserResponse res = userMap.toUserRes(user.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Update user success", res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Can not found user with id" +id, " "));
    }

    @Transactional
    public ResponseEntity<?> updateProfileUser(String id, UserReq userReq) {
        Optional<User> user = userRepository.findUserByIdAndState(id, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            user.get().setName(userReq.getName());
            user.get().setPhone(userReq.getPhone());
            user.get().setProvince(userReq.getProvince());
            user.get().setDistrict(userReq.getDistrict());
            user.get().setWard(userReq.getWard());
            user.get().setAddress(userReq.getAddress());
            userRepository.save(user.get());
            UserResponse userRes = userMap.toUserRes(user.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Update user complete", userRes)
            );

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(true, "Cannot update user ", ""));
    }

    @Transactional
    public ResponseEntity<?> blockUser(String id) {
        Optional<User> user = userRepository.findUserByIdAndState(id, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            user.get().setState(Constant.USER_BLOCK);
            userRepository.save(user.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Delete user success", ""));
        }
        throw new NotFoundException("Can not found user with id " + id + " is activated");
    }

    public ResponseEntity<?> updatePassword(String id, ChangePassReq req) {
        Optional<User> user = userRepository.findUserByIdAndState(id, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            if (!user.get().getSocial().equals(EnumSocial.LOCAL))
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Your account is " +
                        user.get().getSocial() + " account");
            if (passwordEncoder.matches(req.getOldPass(), user.get().getPassword())
                    && !req.getNewPass().equals(req.getOldPass())) {
                user.get().setPassword(passwordEncoder.encode(req.getNewPass()));
                userRepository.save(user.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Change password complete", user));
            } else throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Your old password is wrong" +
                    " or same with new password");
        }
        throw new NotFoundException("Can not found user with id " + id + " is activated");
    }

    @Transactional
    public ResponseEntity<?> resetForgetPassword(String id, ResetForgetPassReq req) {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            if (!foundUser.get().getSocial().equals(EnumSocial.LOCAL))
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Your account is " +
                        foundUser.get().getSocial() + " account");
            foundUser.get().setPassword(passwordEncoder.encode(req.getNewPass()));
            userRepository.save(foundUser.get());
            UserResponse userRes = userMap.toUserRes(foundUser.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Update pass user successfully", userRes));
        }
        throw new NotFoundException("Can not found user with id " + id + " is activated");

    }
}


