package com.newgreen.api;

import com.newgreen.pojo.TbUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户登录接口
 */
public interface UserServiceApi {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/user/login")
    public TbUser login(@RequestParam("username") String username, @RequestParam("password") String password);
}
