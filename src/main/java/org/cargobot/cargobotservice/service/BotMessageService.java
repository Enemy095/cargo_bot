package org.cargobot.cargobotservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotMessageService {

    private final OkHttpTelegramClient telegramClient;



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
            telegramClient.execute(message);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage(), e);
        }
    }

    public void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .replyMarkup(keyboard)
                .build();
        executeMessage(message);
    }

    public void removeReplyKeyboard(Long chatId, String message) {
        ReplyKeyboardRemove removeKeyboard = ReplyKeyboardRemove.builder()
                .removeKeyboard(true)
                .build();


        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .parseMode("Markdown")
                .replyMarkup(removeKeyboard)
                .build();
        executeMessage(msg);
    }

}
