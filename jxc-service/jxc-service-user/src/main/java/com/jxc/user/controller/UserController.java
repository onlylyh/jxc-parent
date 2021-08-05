package com.jxc.user.controller;


import com.jxc.user.entity.User;
import com.jxc.user.service.IUserService;
import org.springframework.web.bind.annotation.*;

import pojos.Result;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lyh
 * @since 2021-08-05
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public Result login(String username, String password, HttpSession session){
        User user = userService.userLogin(username,password);
        session.setAttribute("user",user);
        return new Result(true,"用户登录成功！",user);
    }

    /**
     * 用户信息修改
     * @param
     * @return
     */
    @PostMapping("update")
    public Result update(User user, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        userService.infoUpdate(user,sessionUser.getId());
        session.setAttribute("user",user);
        return new Result(true,"修改成功！",user);
    }

    /**
     * 用户密码修改
     * @param oldPwd
     * @param newPwd
     * @param confirmPwd
     * @return
     */
    @PostMapping("pwdUpdate")
    public Result pwdUpdate(String oldPwd,String newPwd,String confirmPwd,HttpSession session){
        User user = (User) session.getAttribute("user");
        userService.pwdUpdate(oldPwd,newPwd,confirmPwd,user.getUsername());
        session.removeAttribute("user");
        return new Result(true,"修改成功！");
    }

}
