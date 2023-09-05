package com.example.officepcstore.map;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enums.EnumSocial;
import com.example.officepcstore.payload.request.RegisterReq;
import com.example.officepcstore.payload.response.LoginResponse;
import com.example.officepcstore.payload.response.UserResponse;
import com.example.officepcstore.utils.StringUtils;
import org.springframework.stereotype.Service;

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
        }
        return loginRes;
    }

    public User toUser(RegisterReq req) {
        if (req != null) {
            if (!StringUtils.checkPhoneNumberFormat(req.getPhone()))
                throw new AppException(400, "Phone number is invalid!");
            return new User(req.getName(), req.getEmail(), req.getPassword(), req.getPhone(),
                    req.getProvince(), req.getDistrict(), req.getWard(),
                    req.getAddress(), Constant.ROLE_USER, null, Constant.USER_UNVERIFIED, EnumSocial.LOCAL);
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
            userRes.setPhone(user.getPhone());
            userRes.setAddress(user.getAddress());
            userRes.setProvince(user.getProvince());
            userRes.setDistrict(user.getDistrict());
            userRes.setWard(user.getWard());
        }
        return userRes;
    }
}
