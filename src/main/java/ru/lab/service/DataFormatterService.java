package ru.lab.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.lab.dto.TaskDto;
import ru.lab.dto.UserDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Service
public class DataFormatterService {

    private final ObjectMapper objectMapper;

    public DataFormatterService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<UserDto> parseUsers(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<UserDto>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse users JSON", e);
        }
    }

    public List<TaskDto> parseTasks(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            if (root.has("content")) {
                JsonNode contentNode = root.get("content");
                return objectMapper.readValue(contentNode.toString(), new TypeReference<List<TaskDto>>() {});
            }

            throw new RuntimeException("Wrong JSON format: no array or 'content' field");

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse tasks JSON: ", e);
        }
    }


}
