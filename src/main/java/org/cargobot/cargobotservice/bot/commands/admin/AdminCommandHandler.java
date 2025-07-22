package org.cargobot.cargobotservice.bot.commands.admin;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.service.BotMessageService;
import org.cargobot.cargobotservice.service.TariffService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AdminCommandHandler implements CommandHandler {

    private final BotMessageService messageService;
    private final TariffService tariffService;

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

        if (text.equals("/admin")) {
            String message = getFirstMsg();
            messageService.sendText(chatId, message);
            return;
        }

        messageService.sendText(chatId, "Неизвестная команда админа.");
    }

    private String getFirstMsg() {
        return String.format("""
                💼 Админ-панель:

                 📋 /current показать текущий тарий
                 
                 📚 /tariffs показать все тарифы
                 
                 ✨ /create создать тариф
                 
                 🗑️ /delete удалить тариф
                 
                 🔄 /change изменить тариф
                 
                 ✉️ /message отправить сообщение пользователям
                  """);
    }
}

