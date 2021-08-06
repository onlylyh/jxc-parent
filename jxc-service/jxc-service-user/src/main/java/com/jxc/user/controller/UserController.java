package com.jxc.user.controller;


import com.jxc.user.entity.User;
import com.jxc.user.query.UserQuery;
import com.jxc.user.service.IUserService;
import org.springframework.web.bind.annotation.*;

import pojos.Result;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.security.Principal;


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
     * 用户信息修改
     * @param
     * @return
     */
    @PostMapping("update")
    public Result update(User user, Principal principal){
        userService.infoUpdate(user,principal.getName());
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
    public Result pwdUpdate(Principal principal, String oldPwd, String newPwd, String confirmPwd, HttpSession session){
        userService.pwdUpdate(oldPwd,newPwd,confirmPwd,principal.getName());
        session.removeAttribute("user");
        return new Result(true,"用户密码更新成功！！");
    }

    /**
     * 用户列表数据
     * @param userQuery
     * @return
     */
    @GetMapping("list")
    public Result list(UserQuery userQuery){
        return new Result(true,"用户列表数据查询成功",userService.userList(userQuery));
    }

    /**
     * 用户记录添加接口
     * @param user
     * @return
     */
    @PostMapping("saveUser")
    public Result saveUser(User user,Integer[] roleIds){
        userService.saveUser(user,roleIds);
        return new Result(true,"用户添加成功！");
    }

    /**
     * 用户记录更新
     * @param user
     * @return
     */
    @PostMapping("updateUser")
    public Result updateUser(User user,Integer[] roleIds){
        userService.updateUser(user,roleIds);
        return new Result(true,"用户更新成功！");
    }

    /**
     * 用户记录删除
     * @param ids
     * @return
     */
    @GetMapping("delete")
    public Result delete(Integer[] ids){
        userService.deleteUser(ids);
        return new Result(true,"用户记录删除成功！");
    }



}
