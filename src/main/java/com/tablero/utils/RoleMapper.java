package com.tablero.utils;
import com.tablero.model.Role;
public class RoleMapper {
    
    public static Role stringToRole(String roleString){
        return Role.valueOf(roleString);
    }

    public static String roleToString(Role role){
        return role.name();
    }
}
