package org.cargobot.cargobotservice.repository.cash;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.dto.TariffDto;
import org.cargobot.cargobotservice.entity.Tariff;
import org.cargobot.cargobotservice.entity.User;
import org.cargobot.cargobotservice.repository.TariffRepository;
import org.cargobot.cargobotservice.repository.UserRepository;
import org.cargobot.cargobotservice.service.TariffService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TariffCash {
    private final TariffRepository tariffRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init(){
        tariffInit();
        userInit();
    }

    public List<Tariff> tariffInit() {
        return tariffRepository.findAll();
    }


    public Map<Long, User> userInit() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.toMap(
                        User::getChatId,
                        user -> user
                ));
    }

}
