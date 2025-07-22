package org.cargobot.cargobotservice.bot.commands;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.bot.CargoBot;
import org.cargobot.cargobotservice.entity.Tariff;
import org.cargobot.cargobotservice.service.TariffService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AdminCommandHandler implements CommandHandler {

    private final CargoBot bot;
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
        if(!chatId.equals(adminId)) {
            bot.sendText(chatId, "⛔ Неизвестная команда. Используйте /calc.");
            return;
        }

        if(text.equals("/admin")) {
            String message = getFirstMsg();
            bot.sendText(chatId, message);
            return;
        }

        bot.sendText(chatId, "Неизвестная команда админа.");
    }

    private String getFirstMsg() {
        Tariff tariff = tariffService.getCurrentTariff();
        return String.format("""
                💼 Админ-панель:
                
                
                           Текущая стоимость за кубометр: %.2f USD
                           Текущая стоимость за кг: %.2f USD
                         \s
                           /current показать текущий тарий
                           /all     показать все тарифы
                           /create  создать тариф
                           /delete  удалить тариф
                           /change  изменить тариф
                           ""\"""",tariff.getPricePerCubicMeter(), tariff.getKgRate());
    }
}

