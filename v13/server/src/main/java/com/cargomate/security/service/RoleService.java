package com.cargomate.security.service;

import com.cargomate.security.model.Role;

public interface RoleService
{
    Role findByName(String name);
}
