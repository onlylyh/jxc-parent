package com.jxc.user.service;

import com.jxc.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxc.user.query.UserQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyh
 * @since 2021-08-05
 */
public interface IUserService extends IService<User> {

    User findByUsername(String username);

    void infoUpdate(User user,String username);

    void pwdUpdate(String oldPwd, String newPwd, String confirmPwd,String username);

    Map<String,Object> userList(UserQuery userQuery);

    void saveUser(User user,Integer[] roleIds);

    void updateUser(User user,Integer[] roleIds);

    void deleteUser(Integer[] ids);
}
