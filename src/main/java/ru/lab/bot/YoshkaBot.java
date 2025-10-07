package ru.lab.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.lab.config.YoshkaBotConfig;
import ru.lab.dto.TaskDto;
import ru.lab.dto.UserDto;
import ru.lab.service.DataFilterService;
import ru.lab.service.DataFormatterService;
import ru.lab.service.MessageFormattingService;
import ru.lab.service.YoshkaApiService;

import java.util.List;

@Component

public class YoshkaBot extends TelegramLongPollingBot {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final YoshkaApiService yoshkaApiService;
    private final DataFormatterService dataFormatterService;
    private final MessageFormattingService messageFormattingService;
    private final YoshkaBotConfig yoshkaBotConfig;

    @Autowired
    public YoshkaBot(
            YoshkaApiService yoshkaApiService,
            DataFormatterService dataFormatterService,
            MessageFormattingService messageFormattingService,
            YoshkaBotConfig yoshkaBotConfig

    ) {
        super(yoshkaBotConfig.getToken());
        this.yoshkaApiService = yoshkaApiService;
        this.dataFormatterService = dataFormatterService;
        this.messageFormattingService = messageFormattingService;
        this.yoshkaBotConfig = yoshkaBotConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/id":
                    sendMessage(chatId, chatId.toString());
                    break;
                // ...
            }
        }
    }

//    @Scheduled(cron = "0 */10 * * * ?", zone = "UTC")
    @Scheduled(cron = "0 0 6 * * *", zone = "UTC")
    public void sendDailyMessage() {
        final String usersData = yoshkaApiService.getUsersData();
        final String tasksData = yoshkaApiService.getTasksData();

        if (usersData == null || usersData.isBlank() || tasksData == null || tasksData.isBlank()) {
            LOGGER.error("Received empty data from API. Skipping message.");
            sendMessage(yoshkaBotConfig.getChatId(), "Ошибка: не удалось получить данные для отчёта.");
            return;
        }

        final List<UserDto> users = dataFormatterService.parseUsers(usersData);
        final List<TaskDto> tasks = dataFormatterService.parseTasks(tasksData);

        String message = messageFormattingService.formatDataToChatBot(users,  tasks);
        sendMessage(yoshkaBotConfig.getChatId(), message);
    }

    private void sendMessage(Long chatId, String message){
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send message in chat {}, error: {}", chatId, e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return yoshkaBotConfig.getBotUsername();
    }

}
