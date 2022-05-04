package com.newgreen.passportservice.service.impl;

import com.newgreen.mapper.TbUserMapper;
import com.newgreen.passportservice.service.UserService;
import com.newgreen.pojo.TbUser;
import com.newgreen.pojo.TbUserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper userMapper;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public TbUser login(String username, String password) {
        TbUserExample exp = new TbUserExample();
        exp.createCriteria().andUsernameEqualTo(username).andPasswordEqualTo(password);
        List<TbUser> users = (List<TbUser>) userMapper.selectByExample(exp);
        if (users!=null&&users.size()>0){
            return users.get(0);
        }
        return null;
    }
}
