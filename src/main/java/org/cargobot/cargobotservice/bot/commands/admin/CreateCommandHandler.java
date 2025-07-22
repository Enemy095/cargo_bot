package org.cargobot.cargobotservice.bot.commands.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.dto.TariffDto;
import org.cargobot.cargobotservice.dto.session.TariffSession;
import org.cargobot.cargobotservice.entity.states.AdminTariffState;
import org.cargobot.cargobotservice.service.BotMessageService;
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

    private final BotMessageService messageService;
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
            messageService.sendText(chatId, "⛔ Неизвестная команда. Используйте /calc.");
            return;
        }

        TariffSession tariffSession = tariffSessionService.getTariffSession(chatId);

        if (text.equalsIgnoreCase("/create")) {
            tariffSession.setState(AdminTariffState.AWAIT_TARIFF_NAME);
            messageService.sendText(chatId, "Введите название тарифа: ");
            return;
        }
        switch (tariffSession.getState()) {
            case AWAIT_TARIFF_NAME -> {
                try {
                    tariffSession.setTariffName(text);
                    tariffSession.setState(AdminTariffState.AWAIT_KG_RATE);
                    messageService.sendText(chatId, "Введите цену за киллограмм:");
                } catch (NumberFormatException e) {
                    messageService.sendText(chatId, "⛔ Введите название тарифа:");
                }
            }
            case AWAIT_KG_RATE -> {
                try {
                    tariffSession.setKgRate(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_MIN_PRICE);
                    messageService.sendText(chatId, "Введите минимальную цену:");
                } catch (NumberFormatException e) {
                    messageService.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_MIN_PRICE -> {
                try {
                    tariffSession.setMinPrice(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_CUBIC_CONVERSION);
                    messageService.sendText(chatId, "Введите коэфициент для объемного веса:");
                } catch (NumberFormatException e) {
                    messageService.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_CUBIC_CONVERSION -> {
                try {
                    tariffSession.setCubicConversion(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_FRAGILITY);
                    messageService.sendText(chatId, "Введите коэфициент для хрупкого товара в процентах %:");
                } catch (NumberFormatException e) {
                    messageService.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_FRAGILITY -> {
                try {
                    tariffSession.setFragility(Double.parseDouble(text));
                    tariffSession.setState(AdminTariffState.AWAIT_URGENCY);
                    //ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    messageService.sendText(chatId, "Введите коэфициент для срочной доставки в процентах %:");
                } catch (NumberFormatException e) {
                    messageService.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_URGENCY -> {
                try {
                    tariffSession.setUrgency(Double.parseDouble(text));
                    ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    tariffSession.setState(AdminTariffState.AWAIT_ACTIVE);
                    messageService.sendMessageWithKeyboard(chatId, "Сделать тариф активным:", keyboard);//, keyboard);
                } catch (RuntimeException e) {
                    messageService.sendText(chatId, "⛔ Введите число!");
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

                    messageService.removeReplyKeyboard(chatId, resultMessage);
                    log.info("new tariff {}", resultMessage);
                    tariffSessionService.clearUserSession(chatId);

                } catch (RuntimeException e) {
                    messageService.sendText(chatId, """
                            ⛔ Ответьте "ДА" или "НЕТ"\s""");
                }
            }
            default -> messageService.sendText(chatId, "Введите /calc для начала расчёта.");
        }
    }

    private boolean isInCalculation(Update update) {
        Long chatId = update.getMessage().getChatId();
        TariffSession session = tariffSessionService.getTariffSession(chatId);
        return session.getState() != AdminTariffState.NONE;
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

