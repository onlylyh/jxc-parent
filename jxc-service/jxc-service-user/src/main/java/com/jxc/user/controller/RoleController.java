package com.jxc.user.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.jxc.user.entity.Role;
import com.jxc.user.query.RoleQuery;
import com.jxc.user.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojos.Result;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 角色列表数据动态查询
     * @param roleQuery
     * @return
     */
    @GetMapping("list")
    public Result listRole(RoleQuery roleQuery){
        Map<String, Object> map = roleService.listRole(roleQuery);
        return new Result(true,"用户列表数据查询成功！",map);
    }

    /**
     * 角色添加接口
     * @param role
     * @return
     */
    @PostMapping("save")
    public Result save(Role role){
        roleService.saveRole(role);
        return new Result(true,"添加角色成功！");
    }

    /**
     * 角色删除接口
     * @param ids
     * @return
     */
    @GetMapping("delete")
    public Result delete(Integer[] ids){
        roleService.delete(ids);
        return new Result(true,"角色删除成功！");
    }

    /**
     * 角色修改
     * @param role
     * @return
     */
    @PostMapping("update")
    public Result update(Role role){
        roleService.updateRole(role);
        return new Result(true,"角色信息修改成功！");
    }
}
