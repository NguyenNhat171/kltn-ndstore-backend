package com.example.officepcstore.map;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enums.EnumGender;
import com.example.officepcstore.models.enums.EnumSocial;
import com.example.officepcstore.payload.request.RegisterReq;
import com.example.officepcstore.payload.response.LoginResponse;
import com.example.officepcstore.payload.response.UserResponse;
import com.example.officepcstore.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserMap {
    public LoginResponse toLoginRes(User user) {
        LoginResponse loginRes = new LoginResponse();
        if (user != null) {
            loginRes.setId(user.getId());
            loginRes.setName(user.getName());
            loginRes.setEmail(user.getEmail());
            loginRes.setAvatar(user.getAvatar());
            loginRes.setRole(user.getRole());
            loginRes.setGender(user.getGender());
        }
        return loginRes;
    }

    public User toUser(RegisterReq req) {
        if (req != null) {
            EnumGender gender;
            if (!StringUtils.isPhoneNumberFormat(req.getPhone()))
                throw new AppException(400, "Phone number is invalid!");
            try {
                gender = EnumGender.valueOf(req.getGender());
            } catch (IllegalArgumentException e) {
                throw new AppException(400, "Gender is invalid!");
            }
            return new User(req.getName(), req.getEmail(), req.getPassword(), req.getPhone(),
                    req.getProvince(), req.getDistrict(), req.getWard(),
                    req.getAddress(), Constant.ROLE_USER, null,
                    gender, Constant.USER_UNVERIFIED, EnumSocial.LOCAL);
        }
        return null;
    }


    public UserResponse toUserRes(User user) {
        UserResponse userRes = new UserResponse();
        if (user != null) {
            userRes.setId(user.getId());
            userRes.setName(user.getName());
            userRes.setEmail(user.getEmail());
            userRes.setAvatar(user.getAvatar());
            userRes.setRole(user.getRole());
            userRes.setState(user.getState());
            userRes.setGender(user.getGender());
            userRes.setPhone(user.getPhone());
            userRes.setAddress(user.getAddress());
            userRes.setProvince(user.getProvince());
            userRes.setDistrict(user.getDistrict());
            userRes.setWard(user.getWard());
        }
        return userRes;
    }
}
