package com.example.officepcstore.security.oauth;


import com.example.officepcstore.config.Constant;
import com.example.officepcstore.map.UserMap;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enums.EnumGender;
import com.example.officepcstore.models.enums.EnumSocial;
import com.example.officepcstore.payload.response.LoginResponse;
import com.example.officepcstore.repository.UserRepository;
import com.example.officepcstore.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class Success extends SavedRequestAwareAuthenticationSuccessHandler {
    private UserRepository userRepository;
    private JwtUtils jwtUtil;
    private UserMap userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        EnumSocial provider = EnumSocial.valueOf(oauth2User.getOauth2ClientName().toUpperCase());

        Optional<User> user = userRepository.findUserByEmailAndState(oauth2User.getEmail(), Constant.USER_ACTIVE);
        if (user.isEmpty()) {
            String accessToken = processAddUser(oauth2User, provider);
            response.sendRedirect(generateRedirectURL(true, accessToken, provider, ""));
        } else {
            try {
                if (EnumUtils.isValidEnum(EnumSocial.class, user.get().getProvider().name()) &&
                        !user.get().getProvider().equals(EnumSocial.LOCAL) &&
                        provider.equals(user.get().getProvider())) {
                    String accessToken = jwtUtil.generateTokenFromUserId(user.get());
                    response.sendRedirect(generateRedirectURL(true, accessToken, provider, ""));
                } else response.sendRedirect(generateRedirectURL(false, "",
                        user.get().getProvider(), user.get().getEmail() + " already have an account with +"+
                                user.get().getProvider() +" method"));
            } catch (NullPointerException e) {
                response.sendRedirect(generateRedirectURL(false, "",
                        user.get().getProvider(), user.get().getEmail() + " already have an account with +"+
                                user.get().getProvider() +" method"));
            }
        }
    }

    public String processAddUser(CustomOAuth2User oAuth2User, EnumSocial social) {
        User newUser = new User(oAuth2User.getName(), oAuth2User.getEmail(), "",
                " ", 0, 0, 0, "unknown", Constant.ROLE_USER,
                oAuth2User.getProfilePicture(), EnumGender.OTHER, Constant.USER_ACTIVE,social);
        userRepository.save(newUser);
        String accessToken = jwtUtil.generateTokenFromUserId(newUser);
        LoginResponse res = userMapper.toLoginRes(newUser);
        res.setAccessToken(accessToken);
        return accessToken;
    }

    public String generateRedirectURL(Boolean success, String token, EnumSocial provider, String message) {
        logger.debug(message);
        String CLIENT_HOST_REDIRECT = "http://localhost:3000/oauth2/redirect?token=";
        return CLIENT_HOST_REDIRECT + token + "&success=" + success + "&provider=" + provider.toString();
    }
}
