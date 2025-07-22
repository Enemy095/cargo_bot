package org.cargobot.cargobotservice.service.session;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.entity.states.CalcUserState;
import org.cargobot.cargobotservice.dto.session.UserSession;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public UserSession getUserSession(Long chatId) {
        return userSessions.computeIfAbsent(chatId, id -> {
            UserSession session = new UserSession();
            session.setChatId(chatId);
            session.setState(CalcUserState.NONE);
            return session;
        });
    }

    public void clearUserSession(Long chatId) {
        userSessions.remove(chatId);
    }
}
