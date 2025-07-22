package org.cargobot.cargobotservice.service.session;

import org.cargobot.cargobotservice.dto.session.TariffDeleteSession;
import org.cargobot.cargobotservice.dto.session.TariffSession;
import org.cargobot.cargobotservice.entity.states.DeleteTariffState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TariffDeleteSessionService {
    private final Map<Long, TariffDeleteSession> tariffSessions = new ConcurrentHashMap<>();

    public TariffDeleteSession getTariffSessionDelete(Long chatId) {
        return tariffSessions.computeIfAbsent(chatId, id -> {
            TariffDeleteSession session = new TariffDeleteSession();
            session.setChatId(chatId);
            session.setDeleteState(DeleteTariffState.NONE);
            return session;
        });
    }

    public void clearUserSession(Long chatId) {
        tariffSessions.remove(chatId);
    }

}
