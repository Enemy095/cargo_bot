package org.cargobot.cargobotservice.bot.commands.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cargobot.cargobotservice.bot.commands.CommandHandler;
import org.cargobot.cargobotservice.dto.CargoCalculationRequest;
import org.cargobot.cargobotservice.dto.CargoCalculationResult;
import org.cargobot.cargobotservice.dto.session.UserSession;
import org.cargobot.cargobotservice.entity.states.CalcUserState;
import org.cargobot.cargobotservice.service.CalculationService;
import org.cargobot.cargobotservice.service.TariffService;
import org.cargobot.cargobotservice.service.TelegramBotService;
import org.cargobot.cargobotservice.service.session.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalcCommandHandler implements CommandHandler {
    private final TelegramBotService bot;
    private final UserSessionService userSessionService;
    private final CalculationService calculationService;
    private final TariffService tariffService;
    private static final String BOT_VERSION = "Бот версия";

    public boolean handleCommand(Update update) {
        if (!update.hasMessage()) return false;

        Message message = update.getMessage();

        // 1. Команда /calc
        if (message.hasText() && message.getText().equalsIgnoreCase("/calc")
                || message.hasText() && message.getText().equalsIgnoreCase(BOT_VERSION)) {
            return true;
        }

        // 2. Сообщение из процесса калькуляции
        if (message.hasText() && isInCalculation(update) && !message.getText().startsWith("/")) {
            return true;
        }

        // 3. Пришли данные из WebApp
        if (message.getWebAppData() != null) {
            return true;
        }

        return false;
    }

    @Override
    public void handle(Update update) {
        log.info("Calc command handled");
        Message message = update.getMessage();
        String text = message.getText();
        System.out.println(message.getChatId());
        Long chatId = message.getChatId();

        if (message.getWebAppData() != null) {
            webFormat(message, chatId);
            return;
        }

        if (message.hasText() && text.equalsIgnoreCase("/calc")) {
            sendWebAppButton(chatId);
            return;
        }

        UserSession userSession = userSessionService.getUserSession(chatId);
        if (text.equalsIgnoreCase(BOT_VERSION)){
            userSession.setState(CalcUserState.AWAIT_LENGTH);
            bot.removeReplyKeyboard(chatId, " \uD83D\uDCCF  Введите длину коробки (см): ");
            return;
        }

        botFormat(text, chatId, userSession);

    }

    private boolean isInCalculation(Update update) {
        Long chatId = update.getMessage().getChatId();
        UserSession session = userSessionService.getUserSession(chatId);
        return session.getState() != CalcUserState.NONE;
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

    public void sendWebAppButton(Long chatId) {
        WebAppInfo webAppInfo = new WebAppInfo("https://enemy095.github.io/");
        KeyboardButton webAppButton = KeyboardButton.builder()
                .text("Открыть форму")
                .webApp(webAppInfo)
                .build();

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(webAppButton);
        row.add(BOT_VERSION);
        rows.add(row);

        ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .build();

        bot.sendMessageWithKeyboard(chatId,
                "Нажмите кнопку ниже, чтобы открыть форму:",
                markup);
    }

    private void webFormat(Message message, Long chatId) {
        String webAppData = message.getWebAppData().getData();
        log.info("Получены данные из WebApp: {}", webAppData);
        bot.removeReplyKeyboard(message.getChatId(), "*✅ Данные успешно получены!*\n```\n" + webAppData + "\n```");
        try {
            ObjectMapper mapper = new ObjectMapper();
            CargoCalculationRequest data = mapper.readValue(webAppData, CargoCalculationRequest.class);

            // Теперь вы можете работать с данными
            log.info("Parsed data: {}", data);
            bot.sendText(chatId, "Стоимость доставки: ");

        } catch (JsonProcessingException e) {
            log.error("Ошибка парсинга JSON", e);
            bot.sendText(chatId, "❌ Ошибка обработки данных");
        }
    }

    private void botFormat(String text, Long chatId, UserSession userSession) {

        switch (userSession.getState()) {
            case AWAIT_LENGTH -> {
                try {
                    userSession.setLength(Integer.parseInt(text));
                    userSession.setState(CalcUserState.AWAIT_WIDTH);
                    bot.sendText(chatId, " \uD83D\uDCCF  Введите ширину коробки (см):");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_WIDTH -> {
                try {
                    userSession.setWidth(Integer.parseInt(text));
                    userSession.setState(CalcUserState.AWAIT_HEIGHT);
                    bot.sendText(chatId, " \uD83D\uDCCF  Введите высоту коробки (см):");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_HEIGHT -> {
                try {
                    userSession.setHeight(Integer.parseInt(text));
                    userSession.setState(CalcUserState.AWAIT_WEIGHT);
                    bot.sendText(chatId, " \uD83C\uDFCB\uFE0F  Введите вес одной коробки (кг):");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_WEIGHT -> {
                try {
                    userSession.setWeight(Double.parseDouble(text));
                    userSession.setState(CalcUserState.AWAIT_BOXES);
                    bot.sendText(chatId, " \uD83D\uDCE6  Введите количество коробок:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
                }
            }
            case AWAIT_BOXES -> {
                try {
                    userSession.setQuantity(Integer.parseInt(text));
                    userSession.setState(CalcUserState.CHECK_FRAGILITY);
                    ReplyKeyboardMarkup keyboard = createYesNoKeyboard();
                    bot.sendMessageWithKeyboard(chatId, "🍸 Хрупкий товар:", keyboard);
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "⛔ Введите число!");
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
                    userSession.setState(CalcUserState.CHECK_URGENCY);
                    bot.sendMessageWithKeyboard(chatId, " \uD83C\uDFCE Срочно:", keyboard);
                } catch (RuntimeException e) {
                    bot.sendText(chatId, """
                            ⛔ Ответьте "ДА" или "НЕТ"\s""");
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
                    request.setLength(userSession.getLength());
                    request.setWidth(userSession.getWidth());
                    request.setHeight(userSession.getHeight());
                    request.setWeight(userSession.getWeight());
                    request.setBoxCount(userSession.getQuantity());
                    request.setUrgency(userSession.isUrgency());
                    request.setFragile(userSession.isFragile());
                    request.setTariff(tariffService.getTariffByName("number 1"));

                    CargoCalculationResult result = calculationService.calculatePrice(request);

                    String resultMessage = String.format("""
                            ✅ *Результаты расчета:*

                            *Объем: %.3f м³*

                            *Общий вес: %.2f кг*

                            *Примерная стоимость: %.2f USD* 💵

                            """, result.getTotalVolume(), result.getTotalWeight(), result.getCost());
                    bot.removeReplyKeyboard(chatId, resultMessage);
                    log.info("Calculation result: {}", resultMessage);
                    userSessionService.clearUserSession(chatId);
                } catch (RuntimeException e) {
                    bot.sendText(chatId, """
                            ⛔ Ответьте "ДА" или "НЕТ"\s""");
                }
            }
            default -> bot.sendText(chatId, "Введите /calc для начала расчёта.");
        }
    }

}

