package com.mayur.DesiCart.shop.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mayur.DesiCart.shop.user.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
