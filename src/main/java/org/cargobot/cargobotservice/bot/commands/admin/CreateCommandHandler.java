package org.cargobot.cargobotservice.bot.commands.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.CargoBot;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.dto.TariffDto;
import org.cargobot.cargobotservice.dto.session.TariffSession;
import org.cargobot.cargobotservice.entity.states.AdminTariffState;
import org.cargobot.cargobotservice.service.TariffService;
import org.cargobot.cargobotservice.service.session.TariffSessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCommandHandler implements CommandHandler {

    private final CargoBot bot;
    private final TariffSessionService tariffSessionService;
    private final TariffService tariffService;

    @Value("${admin.id}")
    private Long adminId;

    @Override
    public boolean handleCommand(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().equalsIgnoreCase("/create") ||
                isInCalculation(update) && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        System.out.println(chatId);
        if (!chatId.equals(adminId)) {
            bot.sendText(chatId, "⛔ Неизвестная команда. Используйте /calc.");
            return;
        }

        TariffSession tariffSession = tariffSessionService.getTariffSession(chatId);

        if (text.equalsIgnoreCase("/create")) {
            tariffSession.setState(AdminTariffState.AWAIT_TARIFF_NAME);
            bot.sendText(chatId, "Введите название тарифа: ");
            return;
        }
        switch (tariffSession.getState()) {
            case AWAIT_TARIFF_NAME -> {
                try {
                    tariffSession.setTariffName(text);
                    tariffSession.setState(AdminTariffState.AWAIT_KG_RATE);
                    bot.sendText(chatId, "Введите цену за киллограмм:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите название тарифа:");
                }
            }
            case AWAIT_KG_RATE -> {
                try {
                    tariffSession.setKgRate(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_MIN_PRICE);
                    bot.sendText(chatId, "Введите минимальную цену:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_MIN_PRICE -> {
                try {
                    tariffSession.setMinPrice(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_CUBIC_CONVERSION);
                    bot.sendText(chatId, "Введите коэфициент для объемного веса:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_CUBIC_CONVERSION -> {
                try {
                    tariffSession.setCubicConversion(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_FRAGILITY);
                    bot.sendText(chatId, "Введите коэфициент для хрупкого товара в процентах %:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_FRAGILITY -> {
                try {
                    tariffSession.setFragility(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_URGENCY);
                    //ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    bot.sendText(chatId, "Введите коэфициент для срочной доставки в процентах %:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_URGENCY -> {
                try {
                    tariffSession.setUrgency(Double.parseDouble(text));
                    ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    tariffSession.setState(AdminTariffState.AWAIT_ACTIVE);
                    bot.sendMessageWithKeyboard(chatId, "Сделать тариф активным:", keyboard);//, keyboard);
                } catch (RuntimeException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_ACTIVE -> {
                try {
                    if (text.equalsIgnoreCase("да")) {
                        tariffSession.setActive(true);
                    } else if (text.equalsIgnoreCase("нет")) {
                        tariffSession.setActive(false);
                    } else {
                        throw new RuntimeException();
                    }

                    TariffDto tariffDto = new TariffDto();
                    tariffDto.setName(tariffSession.getTariffName());
                    tariffDto.setKgRate(tariffSession.getKgRate());
                    tariffDto.setMinPrice(tariffSession.getMinPrice());
                    tariffDto.setCubicConversion(tariffSession.getCubicConversion());
                    tariffDto.setFragility(tariffSession.getFragility());
                    tariffDto.setUrgency(tariffSession.getUrgency());
                    tariffDto.setActive(tariffSession.isActive());

                    String resultMessage = tariffService.createTariff(tariffDto);

                    bot.removeReplyKeyboard(chatId, resultMessage);
                    log.info("new tariff {}", resultMessage);
                    tariffSessionService.clearUserSession(chatId);

                } catch (RuntimeException e) {
                    bot.sendText(chatId, """
                            ⛔ Ответьте "ДА" или "НЕТ"\s""");
                }
            }
            default -> bot.sendText(chatId, "Введите /calc для начала расчёта.");
        }
    }

    private boolean isInCalculation(Update update) {
        Long chatId = update.getMessage().getChatId();
        TariffSession session = tariffSessionService.getTariffSession(chatId);
        return session.getState() != AdminTariffState.NONE;
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

