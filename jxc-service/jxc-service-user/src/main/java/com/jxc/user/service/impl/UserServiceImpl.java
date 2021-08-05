package com.jxc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxc.user.entity.User;
import com.jxc.user.mapper.UserMapper;
import com.jxc.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lyh
 * @since 2021-08-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User userLogin(String username, String password) {
        /**
         * 用户名不为空 密码不为空
         * 根据用户名查找返回的对象不为Null
         * 密码比对正确
         */
        Assert.isTrue(StringUtils.isNotEmpty(username),"用户名不能为空");
        Assert.isTrue(StringUtils.isNotEmpty(password),"密码不能为空");
        User user = this.findByUsername(username);
        Assert.isTrue(user!=null,"用户名不存在或已注销！");
        Assert.isTrue(user.getPassword().equals(password),"密码错误");
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return this.baseMapper.selectOne(new QueryWrapper<User>().eq("is_del",0).eq("username",username));
    }

    @Override
    @Transactional
    public void infoUpdate(User user,Integer id) {
        /**
         * 用户名 非空
         * 真实姓名非空
         */
        Assert.isTrue(StringUtils.isNotEmpty(user.getUsername()),"用户名不能为空！");
        Assert.isTrue(StringUtils.isNotEmpty(user.getTrueName()),"真实姓名不能为空！");
        user.setId(id);
        this.baseMapper.updateById(user);
    }

    @Override
    @Transactional
    public void pwdUpdate(String oldPwd, String newPwd, String confirmPwd,String username) {
        /**
         * 新密码非空
         * 新密码与确认密码比对
         * 新密码与旧密码不相同
         * 旧密码是否正确
         */
        Assert.isTrue(StringUtils.isNotEmpty(newPwd),"密码不能为空！");
        Assert.isTrue(newPwd.equals(confirmPwd),"新密码与确认密码不一致！");
        Assert.isTrue(!oldPwd.equals(newPwd),"旧密码与新密码不能相同！");
        User user = this.findByUsername(username);
        Assert.isTrue(user.getPassword().equals(oldPwd),"密码不正确！");
        user.setPassword(newPwd);
        this.baseMapper.updateById(user);
    }
}
