package com.example.officepcstore.service;


import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.UserMap;
import com.example.officepcstore.models.enity.Token;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enums.EnumMailType;
import com.example.officepcstore.models.enums.EnumSocial;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.LoginReq;
import com.example.officepcstore.payload.request.RegisterReq;
import com.example.officepcstore.payload.request.VerifyReq;
import com.example.officepcstore.payload.response.LoginResponse;
import com.example.officepcstore.repository.UserRepository;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.security.user.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMap userMapper;
    private final MailService mailService;


    public ResponseEntity<?> loginAccount(LoginReq req) {
        Optional<User> userChecker = userRepository.findUserByEmail(req.getEmail());
        if(userChecker.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Email or Password is wrong", ""));
        }
        else if (userChecker.get().getState().equals(Constant.USER_BLOCK)) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Your account is block", ""));
         }
        else if(userChecker.get().getState().equals(Constant.USER_UNVERIFIED)) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Your account is unconfirm", ""));
        }
            else{
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

                if (user.getUser().getProvider().equals(EnumSocial.LOCAL)) {

                    LoginResponse res = userMapper.toLoginRes(user.getUser());
                    String access_token = jwtUtils.generateTokenFromUserId(user.getUser());
                    res.setAccessToken(access_token);
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObjectData(true, "Log in successfully ", res));
                } else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Your account is " +
                        user.getUser().getProvider() + " account");
            } catch (BadCredentialsException ex) {
//            ex.printStackTrace();
                throw new BadCredentialsException(ex.getMessage());
            }
        }
    }


    public ResponseEntity<?> registerAccount(RegisterReq req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new AppException(HttpStatus.CONFLICT.value(), "Email already exists");
        req.setPassword(passwordEncoder.encode(req.getPassword()));
        User user = userMapper.toUser(req);
        if (user != null) {
            try {
                sendVerifyMail(user);
            } catch (Exception e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObjectData(true, "Register successfully ", "")
        );
    }
    public ResponseEntity<?> sendMailGetOTP(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            try {
                sendVerifyMail(user.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Send otp email success", email));
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Failed to send reset email");
            }
        }
        throw new NotFoundException("Can not found user with email " + email + " is activated");
    }


    public ResponseEntity<?> sendMailResetForgetPass(String email) {
        Optional<User> user = userRepository.findUserByEmailAndState(email, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            if (user.get().getProvider().equals(EnumSocial.LOCAL)) {
                try {
                    sendVerifyMailReset(user.get());
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObjectData(true, "Send email reset password success", email));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Failed");
                }
            } else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Your account is " +
                    user.get().getProvider() + " account");
        }
        throw new NotFoundException("Can not found user with email " + email + " is activated");
    }

    @SneakyThrows
    public void sendVerifyMail(User user) {
        String token = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        Map<String, Object> model = new HashMap<>();
        model.put("token", token);
        user.setToken(new Token(token, LocalDateTime.now().plusMinutes(10)));
        userRepository.save(user);
        mailService.sendEmail(user.getEmail(), model, EnumMailType.AUTH);
    }
    @SneakyThrows
    public void sendVerifyMailReset(User user) {
        String token = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        Map<String, Object> model = new HashMap<>();
        model.put("token", token);
        user.setToken(new Token(token, LocalDateTime.now().plusMinutes(10)));
        userRepository.save(user);
        mailService.sendEmail(user.getEmail(), model, EnumMailType.RESET);
    }



    public ResponseEntity<?> verifyOTP(VerifyReq req) {
        switch (req.getType().toLowerCase()) {
            case "register":
                return verifyRegister(req.getEmail(), req.getOtp());
            case "reset":
                return verifyReset(req.getEmail(), req.getOtp());
            default:
                throw new NotFoundException("Can not found type of verify");
        }
    }

    private ResponseEntity<?> verifyReset(String email, String otp) {
        Optional<User> user = userRepository.findUserByEmailAndState(email, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            if (!user.get().getProvider().equals(EnumSocial.LOCAL)) throw new AppException(HttpStatus.BAD_REQUEST.value(), "Your account is " +
                    user.get().getProvider() + " account");
            Map<String, Object> res = new HashMap<>();
            boolean verify = false;
            if (LocalDateTime.now().isBefore(user.get().getToken().getExp())) {
                if (user.get().getToken().getOtp().equals(otp)) {
                    res.put("id", user.get().getId());
                    res.put("token", jwtUtils.generateTokenFromUserId(user.get()));
                    verify = true;
                }
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "OTP with email: " + email + " is " + verify, res));
            } else {
                user.get().setToken(null);
                userRepository.save(user.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(false, "OTP with email: " + email + " is expired" , ""));
            }
        }
        throw new NotFoundException("Can not found user with email " + email + " is activated");
    }

    private ResponseEntity<?> verifyRegister(String email, String otp) {
        Optional<User> user = userRepository.findUserByEmailAndState(email, Constant.USER_UNVERIFIED);
        if (user.isPresent()) {
            boolean verify = false;
            if (LocalDateTime.now().isBefore(user.get().getToken().getExp())) {
                if (user.get().getToken().getOtp().equals(otp)) {
                    user.get().setState(Constant.USER_ACTIVE);
                    user.get().setToken(null);
                    userRepository.save(user.get());
                    verify = true;
                }
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(verify, "OTP with email: " + email + " is " + verify, ""));
            } else {
                user.get().setToken(null);
                userRepository.save(user.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(false, "OTP with email: " + email + " is expired" , ""));
            }
        }
        throw new NotFoundException("Can not found user with email " + email);
    }

}
