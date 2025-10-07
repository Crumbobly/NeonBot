package ru.lab.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "telegram.bot")
@Validated
public final class YoshkaBotConfig {

    @NotBlank
    private final String botUsername;

    @NotBlank
    private final String token;

    @NotNull
    private final Long chatId;

    @ConstructorBinding
    public YoshkaBotConfig(String botUsername, String token, Long chatId) {
        this.botUsername = botUsername;
        this.token = token;
        this.chatId = chatId;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getToken() {
        return token;
    }

    public Long getChatId() {
        return chatId;
    }
}
