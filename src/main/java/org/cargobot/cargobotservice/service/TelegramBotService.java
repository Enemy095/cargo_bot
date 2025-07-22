package org.cargobot.cargobotservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class TelegramBotService {

    private final BotMessageService messageService;

    public void sendText(Long chatId, String text) {
        messageService.sendText(chatId, text);
    }

    public void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        messageService.sendMessageWithKeyboard(chatId, text, keyboard);
    }

    public void removeReplyKeyboard(Long chatId, String message) {
        messageService.removeReplyKeyboard(chatId, message);
    }
}
