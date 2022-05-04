package com.newgreen.passportservice.service;

import com.newgreen.pojo.TbUser;

public interface UserService {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public TbUser login(String username, String password);
}
