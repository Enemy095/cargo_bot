package org.cargobot.cargobotservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Configuration
public class OkHttpClient {
    @Bean
    public OkHttpTelegramClient telegramClient(@Value("${telegram.bot.token}") String token) {
        return new OkHttpTelegramClient(token);
    }

}
