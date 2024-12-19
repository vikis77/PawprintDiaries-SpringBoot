package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qin.catcat.unite.param.UpdateRoleParam;
import com.qin.catcat.unite.popo.entity.Role;
import com.qin.catcat.unite.popo.vo.RoleListVO;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 创建角色
     * @param role 角色信息
     * @return 是否创建成功
     */
    boolean createRole(Role role);
    
    /**
     * 更新角色
     * @param role 角色信息
     * @return 是否更新成功
     */
    boolean updateRole(UpdateRoleParam param);
    
    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Long roleId);
    
    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Integer userId);
    
    /**
     * 检查角色编码是否已存在
     * @param roleCode 角色编码
     * @return 是否存在
     */
    boolean checkRoleCodeExists(String roleCode);

    /**
     * 分页获取角色列表（目前只支持获取管理员）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 角色列表
     */
    List<RoleListVO> list(int page, int pageSize);

    /**
     * 搜索用户及其角色
     * @param keyword 关键词
     * @return 用户及其角色列表
     */
    List<RoleListVO> searchUsersAndRoles(String keyword);
} 