package ru.lab.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDto {
    Integer id;
    String code;
    String title;
    List<Integer> executorsIds;
    LocalDate deadlineTs;
    // Тут можно было бы хранить флаг (completed), но сервер всегда возвращает false (даже если задача завершена)
    // Поэтому будем хранить дату её завершения.
    LocalDate completeTs;

    public TaskDto() {
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public List<Integer> getExecutorsIds() {
        return executorsIds;
    }

    public LocalDate getCompleteTs() {
        return completeTs;
    }

    public LocalDate getDeadlineTs() {
        return deadlineTs;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExecutorsIds(List<Integer> executorsIds) {
        this.executorsIds = executorsIds;
    }

    public void setCompleteTs(LocalDate completeTs) {
        this.completeTs = completeTs;
    }

    public void setDeadlineTs(LocalDate deadlineTs) {
        this.deadlineTs = deadlineTs;
    }

    @Override
    public String toString() {
        return "(%s, %s, %s, %s)".formatted(id, title, deadlineTs, completeTs);
    }
}
