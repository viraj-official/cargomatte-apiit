package com.cargomate.security.service.impl;

import com.cargomate.security.repository.RoleRepository;
import com.cargomate.security.model.Role;
import com.cargomate.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleService
{
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findByName(String name)
    {
        Role role = roleRepository.findRoleByName(name);
        return role;
    }
}
