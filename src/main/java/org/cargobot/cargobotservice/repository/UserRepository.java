package org.cargobot.cargobotservice.repository;

import org.cargobot.cargobotservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
