package org.cargobot.cargobotservice.bot.commands;


import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    boolean handleCommand(Update update);

    void handle(Update update);
}
