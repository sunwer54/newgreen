package com.newgreen.passport.passportcontroller;

import com.newgreen.commons.MtResult;
import com.newgreen.passport.service.UserService;
import com.newgreen.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @RequestMapping("/user/login")
    public MtResult login(@RequestParam("username") String username, String password, HttpSession session){
        TbUser tUser = userService.login(username, password);
        System.out.println("用户信息："+tUser);
        if (tUser!=null){
            session.setAttribute("user",tUser);
            return MtResult.ok();
        }
        return MtResult.error("用户名或密码错误");
    }
}
