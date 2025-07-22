package org.cargobot.cargobotservice.service;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.entity.User;
import org.cargobot.cargobotservice.repository.UserRepository;
import org.cargobot.cargobotservice.repository.cash.Cash;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(User user) {
        userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
