package ru.lab.service;


import org.springframework.stereotype.Service;
import ru.lab.dto.TaskDto;
import ru.lab.dto.UserDto;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DataFilterService {

    /**
    Возвращает список незавершенных задач
     */
    public List<TaskDto> filterUnCompletedTask(List<TaskDto> tasks) {
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);

        return tasks.stream()
                .filter(task -> task.getCompleteTs().isAfter(currentDate))
                .toList();
    }

    /**
     Возвращает список просроченных задач
     */
    public List<TaskDto> filterExpiredTasks(List<TaskDto> tasks) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        return tasks.stream()
                .filter(task -> task.getDeadlineTs().isBefore(today))
                .toList();
    }

    /**
     Возвращает список задач с дедлайном - сегодня
     */
    public List<TaskDto> filterTodayDeadlineTasks(List<TaskDto> tasks) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        return tasks.stream()
                .filter(task -> task.getDeadlineTs().equals(today))
                .toList();
    }

    /**
     Возвращает список задач с дедлайном - завтра
     */
    public List<TaskDto> filterTomorrowDeadlineTasks(List<TaskDto> tasks) {
        LocalDate tomorrow = LocalDate.now(ZoneOffset.UTC).plusDays(1);

        return tasks.stream()
                .filter(task -> task.getDeadlineTs().equals(tomorrow))
                .toList();
    }

    /**
     Возвращает список исполнителей задачи
     */
    public List<UserDto> getUsersForTask(TaskDto task, List<UserDto> users){
        List<Integer> ids = task.getExecutorsIds();
        return users.stream().filter(user -> ids.contains(user.getId())).toList();
    }


}
