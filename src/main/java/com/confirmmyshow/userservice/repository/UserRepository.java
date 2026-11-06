package com.confirmmyshow.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.confirmmyshow.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserUuid(String userUuid);

    boolean existsByEmail(String email);
}