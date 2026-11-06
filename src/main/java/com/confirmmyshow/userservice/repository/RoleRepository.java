package com.confirmmyshow.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.confirmmyshow.userservice.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Short> {

    Optional<Role> findByName(String name);
}