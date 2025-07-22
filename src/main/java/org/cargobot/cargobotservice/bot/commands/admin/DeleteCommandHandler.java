package org.cargobot.cargobotservice.bot.commands.admin;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.bot.CargoBot;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.dto.session.TariffDeleteSession;
import org.cargobot.cargobotservice.dto.session.TariffSession;
import org.cargobot.cargobotservice.entity.states.DeleteTariffState;
import org.cargobot.cargobotservice.service.TariffService;
import org.cargobot.cargobotservice.service.session.TariffDeleteSessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteCommandHandler implements CommandHandler {

    private final CargoBot bot;
    private final TariffService tariffService;
    private final TariffDeleteSessionService tariffDeleteSessionService;

    @Value("${admin.id}")
    private Long adminId;

    @Override
    public boolean handleCommand(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().equalsIgnoreCase("/delete") ||
                isInCalculation(update) && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (!chatId.equals(adminId)) {
            bot.sendText(chatId, "⛔ Неизвестная команда. Используйте /calc.");
            return;
        }

        TariffDeleteSession tariffSession = tariffDeleteSessionService.getTariffSessionDelete(chatId);

        if (text.equalsIgnoreCase("/delete")) {
            tariffSession.setDeleteState(DeleteTariffState.AWAIT_DELETE);
            bot.sendText(chatId, "Введите название тарифа: ");
            return;
        }

        switch (tariffSession.getDeleteState()) {
            case AWAIT_DELETE -> {
                try {
                    tariffSession.setDeleteState(DeleteTariffState.FINAL_CHOICE);
                    ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    bot.sendMessageWithKeyboard(chatId, "Вы уверены что хотите удалить этот тариф:", keyboard);
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Введите название тарифа:");
                }
            }
            case FINAL_CHOICE -> {
                try {
                    if (text.equalsIgnoreCase("да")) {

                        tariffService.deleteTariffByName(text);
                        String resultMessage = "⛔ Тариф удален!";
                        bot.removeReplyKeyboard(chatId, resultMessage);

                    } else if (text.equalsIgnoreCase("нет")) {

                        String resultMessage = "✅ Тариф не был удален!";
                        bot.removeReplyKeyboard(chatId, resultMessage);

                    } else {
                        throw new RuntimeException();
                    }

                    tariffDeleteSessionService.clearUserSession(chatId);
                } catch (RuntimeException e) {
                    bot.sendText(chatId, """
                            Ответьте "ДА" или "НЕТ"\s""");
                }
            }
            default -> bot.sendText(chatId, "Введите /calc для начала расчёта.");
        }
    }

    private boolean isInCalculation(Update update) {
        Long chatId = update.getMessage().getChatId();
        TariffDeleteSession session = tariffDeleteSessionService.getTariffSessionDelete(chatId);
        return session.getDeleteState() != DeleteTariffState.NONE;
    }

    private ReplyKeyboardMarkup createYesNoKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Да");
        row.add("Нет");
        rows.add(row);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
}
