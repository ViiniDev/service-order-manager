package com.viinidev.serviceorder.repository;

import com.viinidev.serviceorder.domain.Role;
import com.viinidev.serviceorder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}
