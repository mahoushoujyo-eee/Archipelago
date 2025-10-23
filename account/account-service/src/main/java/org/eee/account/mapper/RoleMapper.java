package org.eee.account.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.eee.model.entity.Role;

import java.util.List;

@Mapper
public interface RoleMapper
{
    void registerRole(Role role);
    List<Role> getRoleByUserId(Long userId);
}
