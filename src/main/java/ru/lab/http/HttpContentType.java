package ru.lab.http;

public enum HttpContentType {
    JSON("application/json"),
    FORM("application/x-www-form-urlencoded");

    private final String value;

    HttpContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
