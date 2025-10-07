package ru.lab.dto;

public class TokenPairDto {
    String accessToken;
    String refreshToken;

    public TokenPairDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
