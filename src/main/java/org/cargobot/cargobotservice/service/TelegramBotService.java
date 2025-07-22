package org.cargobot.cargobotservice.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class TelegramBotService {

    private final CargoBot bot;

    public void sendText(Long chatId, String text) {
        bot.sendText(chatId, text);
    }

    public void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        bot.sendMessageWithKeyboard(chatId, text, keyboard);
    }

    public void removeReplyKeyboard(Long chatId, String message) {
        bot.removeReplyKeyboard(chatId, message);
    }
}
