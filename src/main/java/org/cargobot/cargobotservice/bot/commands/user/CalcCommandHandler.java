package org.cargobot.cargobotservice.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.TelegramBotService;
import org.cargobot.cargobotservice.entity.states.UserState;
import org.cargobot.cargobotservice.dto.CargoCalculationRequest;
import org.cargobot.cargobotservice.dto.CargoCalculationResult;
import org.cargobot.cargobotservice.entity.UserSession;
import org.cargobot.cargobotservice.service.CalculationService;
import org.cargobot.cargobotservice.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalcCommandHandler implements CommandHandler {
    private final TelegramBotService bot;
    private final UserSessionService userSessionService;
    private final CalculationService calculationService;

    public boolean handleCommand(Update update) {
        return (update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().equalsIgnoreCase("/calc")) ||
                isInCalculation(update) && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public void handle(Update update) {
        log.info("Calc command handled");
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        UserSession userSession = userSessionService.getUserSession(chatId);

        if (text.equalsIgnoreCase("/calc")) {
            userSession.setState(UserState.AWAIT_LENGTH);
            bot.sendText(chatId, "Введите длину коробки (см): ");
            return;
        }
        switch (userSession.getState()) {
            case AWAIT_LENGTH -> {
                try {
                    userSession.setLength(Integer.parseInt(text));
                    userSession.setState(UserState.AWAIT_WIDTH);
                    bot.sendText(chatId, "Введите ширину коробки (см):");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Введите число!");
                }
            }
            case AWAIT_WIDTH -> {
                try {
                    userSession.setWidth(Integer.parseInt(text));
                    userSession.setState(UserState.AWAIT_HEIGHT);
                    bot.sendText(chatId, "Введите высоту коробки (см):");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Введите число!");
                }
            }
            case AWAIT_HEIGHT -> {
                try {
                    userSession.setHeight(Integer.parseInt(text));
                    userSession.setState(UserState.AWAIT_WEIGHT);
                    bot.sendText(chatId, "Введите вес одной коробки (кг):");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Введите число!");
                }
            }
            case AWAIT_WEIGHT -> {
                try {
                    userSession.setWeight(Double.parseDouble(text));
                    userSession.setState(UserState.AWAIT_BOXES);
                    bot.sendText(chatId, "Введите количество коробок:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Введите число!");
                }
            }
            case AWAIT_BOXES -> {
                try {
                    userSession.setQuantity(Integer.parseInt(text));
                    userSession.setState(UserState.CHECK_FRAGILITY);
                    ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    bot.sendMessageWithKeyboard(chatId, "Хрупкий товар:", keyboard);
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Введите число!");
                }
            }
            case CHECK_FRAGILITY -> {
                try {

                    if (text.equalsIgnoreCase("да")) {
                        userSession.setFragile(true);
                    } else if (text.equalsIgnoreCase("нет")) {
                        userSession.setFragile(false);
                    } else {
                        throw new RuntimeException();
                    }
                    ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    userSession.setState(UserState.CHECK_URGENCY);
                    bot.sendMessageWithKeyboard(chatId, "Срочно:", keyboard);
                } catch (RuntimeException e) {
                    bot.sendText(chatId, """
                            Ответьте "ДА" или "НЕТ"\s""");
                }
            }
            case CHECK_URGENCY -> {
                try {
                    if (text.equalsIgnoreCase("да")) {
                        userSession.setUrgency(true);
                    } else if (text.equalsIgnoreCase("нет")) {
                        userSession.setUrgency(false);
                    } else {
                        throw new RuntimeException();
                    }
                    CargoCalculationRequest request = new CargoCalculationRequest();
                    request.setLengthCm(userSession.getLength());
                    request.setWidthCm(userSession.getWidth());
                    request.setHeightCm(userSession.getHeight());
                    request.setWeightKg(userSession.getWeight());
                    request.setBoxCount(userSession.getQuantity());

                    CargoCalculationResult result = calculationService.calculatePrice(request);

                    String resultMessage = String.format("""
                            ✅ Результаты расчета:
                            Объем: %.3f м³
                            Общий вес: %.2f кг
                            Примерная стоимость: %.2f USD 💵
                            """, result.getTotalVolume(), result.getTotalWeight(), result.getCost());
                    bot.removeReplyKeyboard(chatId, resultMessage);
                    log.info("Calculation result: {}", resultMessage);
                    userSessionService.clearUserSession(chatId);
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
        UserSession session = userSessionService.getUserSession(chatId);
        return session.getState() != UserState.NONE;
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


//    private void isFragile(String text) {
//
//    }
}
