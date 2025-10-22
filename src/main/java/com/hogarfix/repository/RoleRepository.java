package com.hogarfix.repository;

import com.hogarfix.dto.ERole;
import com.hogarfix.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Método crucial para Spring Security: buscar un rol por su nombre
    
    Role findByName(ERole name);
}