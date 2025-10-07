package ru.lab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lab.dto.TaskDto;
import ru.lab.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageFormattingService {

    private final DataFilterService dataFilterService;

    @Autowired
    public MessageFormattingService(DataFilterService dataFilterService){
        this.dataFilterService = dataFilterService;
    }

    private String formatTasksForDay(List<TaskDto> tasks, List<UserDto> users, String title) {
        if (tasks.isEmpty()) return title + " Тут пуста \n\n";

        StringBuilder message = new StringBuilder(title + "\n");
        for (int i = 0; i < tasks.size(); i++) {
            TaskDto task = tasks.get(i);
            List<UserDto> taskUsers = dataFilterService.getUsersForTask(task, users);
            message.append(i+1)
                    .append(") <b>")
                    .append(task.getCode())
                    .append("</b>: ")
                    .append(task.getTitle())
                    .append(".\n    <b>Исполнители:</b> ");
            message.append(taskUsers.stream()
                    .map(UserDto::getDisplayName)
                    .collect(Collectors.joining(", ")));
            message.append("\n");
        }
        message.append("\n");
        return message.toString();
    }


    public String formatDataToChatBot(List<UserDto> users, List<TaskDto> tasks){

        final List<TaskDto> uncompletedTasks = dataFilterService.filterUnCompletedTask(tasks);

        final List<TaskDto> expiredTasks = dataFilterService.filterExpiredTasks(uncompletedTasks);
        final List<TaskDto> todayTasks = dataFilterService.filterTodayDeadlineTasks(uncompletedTasks);
        final List<TaskDto> tomorrowTasks = dataFilterService.filterTomorrowDeadlineTasks(uncompletedTasks);

        if (expiredTasks.isEmpty() && todayTasks.isEmpty() && tomorrowTasks.isEmpty() ) {
            return "В ближайшие 2 дня нет задач с истекающим сроком.";
        }

        return formatTasksForDay(expiredTasks, users, "Просроченные задачи:") +
                formatTasksForDay(todayTasks, users, "Задачи со сроком - \"сегодня\":") +
                formatTasksForDay(tomorrowTasks, users, "Задачи со сроком - \"завтра\":");
    }


}
