package ru.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    Integer id;
    String displayName;

    UserDto(){

    }

    public Integer getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(id, displayName);
    }
}
