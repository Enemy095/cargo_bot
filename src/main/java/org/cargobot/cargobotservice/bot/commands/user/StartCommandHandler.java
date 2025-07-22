package org.cargobot.cargobotservice.bot.commands.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.entity.User;
import org.cargobot.cargobotservice.service.TelegramBotService;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private final TelegramBotService bot;
    private final UserService userService;

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


        bot.sendText(chatId, text);

        User user = new User(chatId.toString());
        userService.createUser(user);



    }
}
