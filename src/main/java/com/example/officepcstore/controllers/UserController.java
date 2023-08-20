package com.example.officepcstore.controllers;

import com.example.officepcstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/admin/manage/users")
    public ResponseEntity<?> findAll (@RequestParam(value = "state", defaultValue = "") String state,
                                      @PageableDefault(size = 5, sort = "name") @ParameterObject Pageable pageable){
        return userService.findAll(state, pageable);
    }
}
