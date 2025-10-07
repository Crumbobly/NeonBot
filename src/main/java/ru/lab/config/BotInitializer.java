package ru.lab.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.lab.bot.YoshkaBot;



@Component
public class BotInitializer {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final YoshkaBot yoshkaBot;

    @Autowired
    public BotInitializer(YoshkaBot yoshkaBot) {
        this.yoshkaBot = yoshkaBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init()throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            telegramBotsApi.registerBot(yoshkaBot);
        } catch (TelegramApiException e){
            LOGGER.error("Error while initializing telegram bot: {}", e.getMessage());
        }
    }
}
