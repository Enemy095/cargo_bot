package org.cargobot.cargobotservice.bot.commands.admin;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.dto.session.TariffDeleteSession;
import org.cargobot.cargobotservice.entity.states.DeleteTariffState;
import org.cargobot.cargobotservice.service.BotMessageService;
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

    private final BotMessageService messageService;
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
            messageService.sendText(chatId, "⛔ Неизвестная команда. Используйте /calc.");
            return;
        }

        TariffDeleteSession tariffSession = tariffDeleteSessionService.getTariffSessionDelete(chatId);

        if (text.equalsIgnoreCase("/delete")) {
            tariffSession.setDeleteState(DeleteTariffState.AWAIT_DELETE);
            messageService.sendText(chatId, "Введите название тарифа: ");
            return;
        }

        switch (tariffSession.getDeleteState()) {
            case AWAIT_DELETE -> {
                try {
                    tariffSession.setDeleteState(DeleteTariffState.FINAL_CHOICE);
                    ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    messageService.sendMessageWithKeyboard(chatId, "Вы уверены что хотите удалить этот тариф:", keyboard);
                } catch (NumberFormatException e) {
                    messageService.sendText(chatId, "Введите название тарифа:");
                }
            }
            case FINAL_CHOICE -> {
                try {
                    if (text.equalsIgnoreCase("да")) {

                        tariffService.deleteTariffByName(text);
                        String resultMessage = "⛔ Тариф удален!";
                        messageService.removeReplyKeyboard(chatId, resultMessage);

                    } else if (text.equalsIgnoreCase("нет")) {

                        String resultMessage = "✅ Тариф не был удален!";
                        messageService.removeReplyKeyboard(chatId, resultMessage);

                    } else {
                        throw new RuntimeException();
                    }

                    tariffDeleteSessionService.clearUserSession(chatId);
                } catch (RuntimeException e) {
                    messageService.sendText(chatId, """
                            Ответьте "ДА" или "НЕТ"\s""");
                }
            }
            default -> messageService.sendText(chatId, "Введите /calc для начала расчёта.");
        }
    }

    private boolean isInCalculation(Update update) {
        Long chatId = update.getMessage().getChatId();
        TariffDeleteSession session = tariffDeleteSessionService.getTariffSessionDelete(chatId);
        return session.getDeleteState() != DeleteTariffState.NONE;
    }

    private ReplyKeyboardMarkup createYesNoKeyboard() {
        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Да");
        row.add("Нет");
        rows.add(row);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
}
