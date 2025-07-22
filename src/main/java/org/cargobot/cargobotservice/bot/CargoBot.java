package org.cargobot.cargobotservice.bot;

import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.dto.session.UserSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CargoBot extends TelegramLongPollingBot {

    private List<CommandHandler> commandHandlers = new ArrayList<>();
    private UserSession userSession;

    @Value("${telegram.bot.username}")
    private String userName;

    @Value("${telegram.bot.token}")
    private String token;

    public void registerHandlers(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    @Override
    public void onUpdateReceived(Update update) {

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
            sendText(chatId, text);
        }
    }

    public void sendText(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .build();
        executeMessage(message);
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .replyMarkup(keyboard)  // Ключевая строка - добавляем клавиатуру
                .build();
        executeMessage(message);
    }

    public void removeReplyKeyboard(Long chatId, String message) {
        ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
        removeKeyboard.setRemoveKeyboard(true);

        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .parseMode("Markdown")
                .replyMarkup(removeKeyboard)
                .build();
        try {
            execute(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return userName;
    }
}
