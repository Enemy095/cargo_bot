package org.cargobot.cargobotservice.service;


import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.dto.session.TariffSession;
import org.cargobot.cargobotservice.entity.states.AdminTariffState;
import org.cargobot.cargobotservice.entity.states.DeleteTariffState;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TariffSessionService {
    private final Map<Long, TariffSession> tariffSessions = new ConcurrentHashMap<>();

    public TariffSession getTariffSession(Long chatId) {
        return tariffSessions.computeIfAbsent(chatId, id -> {
            TariffSession session = new TariffSession();
            session.setChatId(chatId);
            session.setState(AdminTariffState.NONE);
            return session;
        });
    }

    public TariffSession getTariffSessionDelete(Long chatId) {
        return tariffSessions.computeIfAbsent(chatId, id -> {
            TariffSession session = new TariffSession();
            session.setChatId(chatId);
            session.setDeleteState(DeleteTariffState.NONE);
            return session;
        });
    }

    public void clearUserSession(Long chatId) {
        tariffSessions.remove(chatId);
    }

}
