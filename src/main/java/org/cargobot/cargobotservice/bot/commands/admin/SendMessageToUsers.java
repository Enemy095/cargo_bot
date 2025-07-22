package org.cargobot.cargobotservice.bot.commands.admin;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.entity.User;
import org.cargobot.cargobotservice.service.BotMessageService;
import org.cargobot.cargobotservice.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class SendMessageToUsers implements CommandHandler {
    private final BotMessageService messageService;
    private final UserService userService;

    @Value("${admin.id}")
    private Long adminId;

    @Override
    public boolean handleCommand(Update update) {
        return update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().startsWith("/admin");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        System.out.println(chatId);
        if (!chatId.equals(adminId)) {
            messageService.sendText(chatId, "⛔ Неизвестная команда. Используйте /calc.");
            return;
        }

        for (User user : userService.getUsers()) {
            messageService.sendText(Long.parseLong(user.getChatId()), "Hello");
        }

        messageService.sendText(chatId, "Неизвестная команда админа.");
    }
}
