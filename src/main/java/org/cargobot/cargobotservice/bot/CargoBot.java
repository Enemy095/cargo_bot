package org.cargobot.cargobotservice.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.dto.session.UserSession;
import org.cargobot.cargobotservice.service.BotMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CargoBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotMessageService botMessageService;
    private final List<CommandHandler> commandHandlers;
    private UserSession userSession;

    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public void consume(Update update) {
        for (CommandHandler handler : commandHandlers) {
            if (handler.handleCommand(update)) {
                handler.handle(update);
                return;
            }
        }
        handleUnknownCommand(update);
    }

    private void handleUnknownCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String text = "⛔ Неизвестная команда. Используйте /start или /calc.";
            botMessageService.sendText(chatId, text);
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingSingleThreadUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}


