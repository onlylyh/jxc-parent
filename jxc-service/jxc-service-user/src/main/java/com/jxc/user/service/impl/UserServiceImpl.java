package com.jxc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxc.user.entity.User;
import com.jxc.user.mapper.UserMapper;
import com.jxc.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;

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

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return this.baseMapper.selectOne(new QueryWrapper<User>().eq("is_del",0).eq("username",username));
    }

    @Override
    @Transactional
    public void infoUpdate(User user,String username) {
        /**
         * 用户名 非空
         * 真实姓名非空
         */
        Assert.isTrue(StringUtils.isNotEmpty(user.getUsername()),"用户名不能为空！");
        Assert.isTrue(StringUtils.isNotEmpty(user.getTrueName()),"真实姓名不能为空！");
        User oldUser = this.findByUsername(username);
        Assert.isTrue(oldUser!=null,"用户不存在！");
        user.setId(oldUser.getId());
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
        Assert.isTrue(passwordEncoder.matches(oldPwd,user.getPassword()),"原始密码不正确！");
        user.setPassword(passwordEncoder.encode(newPwd));
        this.baseMapper.updateById(user);
    }
}
