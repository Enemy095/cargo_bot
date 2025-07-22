package org.cargobot.cargobotservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.CargoBot;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotInit {
    private final CargoBot cargoBot;
    private final List<CommandHandler> commandHandlers;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        cargoBot.registerHandlers(commandHandlers);

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(cargoBot);
            log.info("Бот успешно запущен");
        } catch (TelegramApiException e) {
            log.error("Не удалось запустить бота", e);
            throw new RuntimeException(e);
        }
    }
}