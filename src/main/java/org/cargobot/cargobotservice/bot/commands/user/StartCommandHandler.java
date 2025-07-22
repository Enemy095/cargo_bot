package org.cargobot.cargobotservice.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.TelegramBotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private final TelegramBotService bot;

    @Override
    public boolean handleCommand(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().equalsIgnoreCase("/start");
    }

    @Override
    public void handle(Update update) {
        log.info("start command handle");
        Long chatId = update.getMessage().getChatId();
        String text = "Добро пожаловать в Cargo Delivery Bot!  \uD83D\uDE9A\n" +
                "Используйте /calc для расчёта стоимости доставки.\n";

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        bot.sendText(chatId, text);
    }
}
