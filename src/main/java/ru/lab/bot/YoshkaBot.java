package ru.lab.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
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

            LOGGER.info("Update received: {} ", update.getMessage().getText());
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Integer thread = update.getMessage().getMessageThreadId();

            switch (messageText) {

                case "/start":
                    sendMessage(chatId, thread, """
                            Доступные команды:
                            /id — показать ID чата
                            /task_info — информация о задачах
                            /start — показать это сообщение снова
                            
                            Бот будет отправлять сообщение о задачах каждый день в 9 утра по МСК в указанный в его конфиге чат.
                            """);
                    break;

                case "/id":
                    sendMessage(chatId, thread, chatId.toString());
                    break;

                case "/task_info":
                    sendMessage(chatId, thread, collectTaskData());
                    break;
            }
        }
    }

    public String collectTaskData() {
        final String usersData = yoshkaApiService.getUsersData();
        final String tasksData = yoshkaApiService.getTasksData();

        if (usersData == null || usersData.isBlank() || tasksData == null || tasksData.isBlank()) {
            LOGGER.error("Received empty data from API. Skipping message.");
            return "Ошибка: не удалось получить данные для отчёта.";
        }

        final List<UserDto> users = dataFormatterService.parseUsers(usersData);
        final List<TaskDto> tasks = dataFormatterService.parseTasks(tasksData);

        return messageFormattingService.formatDataToChatBot(users, tasks);
    }

//    @Scheduled(cron = "0 */5 * * * ?", zone = "UTC")
    @Scheduled(cron = "0 0 6 * * *", zone = "UTC")
    public void sendDailyMessage() {
        String tasksData = collectTaskData();
        sendMessage(yoshkaBotConfig.getChatId(), yoshkaBotConfig.getMessageThread(), tasksData);
    }

    private void sendMessage(Long chatId, Integer thread, String message) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageThreadId(thread);
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
