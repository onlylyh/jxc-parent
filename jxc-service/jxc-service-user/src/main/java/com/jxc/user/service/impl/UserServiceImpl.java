package com.jxc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxc.user.entity.User;
import com.jxc.user.entity.UserRole;
import com.jxc.user.mapper.UserMapper;
import com.jxc.user.query.UserQuery;
import com.jxc.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxc.user.service.UserRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import utils.PageResultUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Resource
    private UserRoleService userRoleService;

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

    @Override
    public Map<String,Object> userList(UserQuery userQuery) {
        IPage<User> page = new Page<User>(userQuery.getPage(),userQuery.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del",0);
        if (StringUtils.isNotEmpty(userQuery.getUsername())) {
            queryWrapper.like("username",userQuery.getUsername());
        }
        IPage<User> iPage = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtils.getPageResult(iPage.getTotal(),iPage.getRecords());
    }

    @Override
    public void saveUser(User user,Integer[] roleIds) {
        /**
         * 用户名
         *      非空 唯一
         * 密码默认值123456
         * 用户默认未删除
         */
        Assert.isTrue(StringUtils.isNotEmpty(user.getUsername()),"用户名不能为空！");
        Assert.isTrue(this.findByUsername(user.getUsername())==null,"用户名已存在！");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setIsDel(0);
        Assert.isTrue(this.save(user),"用户添加失败！");
        /**
         * 给用户分配角色
         */
        User user1 = this.findByUsername(user.getUsername());
        distributeRole(user1.getId(),roleIds);
    }
    //分配用户角色
    private void distributeRole(Integer userId, Integer[] roleIds) {
        /**
         * 查询用户角色关联表
         * 用户是否存在原始角色，存在则删除重新添加
         * 不存在则直接批量添加
         */
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<UserRole>().eq("user_id", userId);
        int count = userRoleService.count(queryWrapper);
        if (count>0){
            userRoleService.remove(queryWrapper);
        }
        if (roleIds!=null&&roleIds.length>0){
            for (Integer roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                Assert.isTrue(userRoleService.save(userRole),"角色添加失败！");
            }
        }
    }

    @Override
    public void updateUser(User user,Integer[] roleIds) {
        /**
         * 用户名 非空 不可重复
         */
        Assert.isTrue(StringUtils.isNotEmpty(user.getUsername()),"用户名不能为空！");
        User temp = this.findByUsername(user.getUsername());
        Assert.isTrue(!(temp!=null&&!(user.getId().equals(temp.getId()))),"用户名已存在！");
        Assert.isTrue(this.updateById(user),"用户信息修改失败！");

        distributeRole(user.getId(),roleIds);
    }

    @Override
    public void deleteUser(Integer[] ids) {
        Assert.isTrue(ids!=null&&ids.length>0,"请选择要删除的用户id！");
        List<User> users = new ArrayList<User>();
        for (Integer id : ids) {
            User user = this.getById(id);
            user.setIsDel(1);
            users.add(user);
        }
        Assert.isTrue(this.updateBatchById(users),"用户删除成功！");
    }
}
