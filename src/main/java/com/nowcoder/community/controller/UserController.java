package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequire;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/24
 **/
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @LoginRequire
    @RequestMapping("/setting")
    public String getSetting(){
        return "/site/setting";
    }

    /**
     * 通过表单提交的数据，直接使用参数入参。
     * @param users
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @LoginRequire
    @RequestMapping("/uploadpassword")
    public String uploadPassword(User users , String newPassword , String confirmPassword){
        System.out.println(newPassword + "--" + confirmPassword);
        User user = hostHolder.getUser();
        userService.uploadPassword(user.getId() , users.getPassword());
        return "redirect:/index";
    }
}
