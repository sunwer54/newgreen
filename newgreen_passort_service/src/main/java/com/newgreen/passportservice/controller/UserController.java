package com.newgreen.passportservice.controller;

import com.newgreen.api.UserServiceApi;
import com.newgreen.passportservice.service.UserService;
import com.newgreen.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserServiceApi {
    @Autowired
    private UserService userService;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public TbUser login(String username, String password) {
        return userService.login(username, password);
    }
}
