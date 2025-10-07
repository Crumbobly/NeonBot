package ru.lab.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lab.config.YoshkaApiConfig;
import ru.lab.dto.TokenPairDto;
import ru.lab.http.HttpContentType;
import ru.lab.http.HttpMethod;

import java.net.http.HttpResponse;

import static ru.lab.utils.UrlUtils.getCodeFromUrl;
import static ru.lab.utils.UrlUtils.getLoginFormUrlFromBody;
import static ru.lab.utils.UrlUtils.getTokenPairFromBody;


@Service
public class YoshkaApiService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final YoshkaApiConfig yoshkaApiConfig;
    private final RequestService requestService;
    private String accessToken;
    private String refreshToken;

    @Autowired
    public YoshkaApiService(YoshkaApiConfig config, RequestService requestService) {
        this.yoshkaApiConfig = config;
        this.requestService = requestService;
    }

    @PostConstruct
    public void init() {
        login();
    }

    public String getUsersData() {
        return getJsonData(yoshkaApiConfig.getUrls().getUsersUrl());
    }

    public String getTasksData() {
        return getJsonData(yoshkaApiConfig.getUrls().getListUrl());
    }

    private String getJsonData(String url) {

        LOGGER.info("Getting data...");

        HttpResponse<String> response = requestService.sendRequest(url, HttpMethod.GET, HttpContentType.JSON, accessToken, null);

        if (response.statusCode() == 401) {
            LOGGER.warn("Access token expired.");
            refreshAccessToken();
            response = requestService.sendRequest(url, HttpMethod.GET, HttpContentType.JSON, accessToken, null);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Request to " + url + " failed with status: " + response.statusCode());
        }

        LOGGER.info("Data has been received");
        return response.body();
    }

    private void exchangeCodeOnToken(String code) {

        LOGGER.info("Exchanging code on tokens...");

        final String json = """
                {
                  "code": "%s",
                  "redirectUrl": "https://bun.rt.ru/authenticator"
                }
                """.formatted(code);

        final HttpResponse<String> response = requestService.sendRequest(
                yoshkaApiConfig.getUrls().getTokenUrl(), HttpMethod.POST, HttpContentType.JSON, null, json
        );
        if (response.statusCode() != 200) {
            throw new RuntimeException("Token request failed with status: " + response.statusCode());
        }

        final TokenPairDto tokenPairDto = getTokenPairFromBody(response.body());
        if (tokenPairDto.getAccessToken() == null || tokenPairDto.getRefreshToken() == null) {
            throw new RuntimeException("Invalid token response received during token exchange");
        }

        this.accessToken = tokenPairDto.getAccessToken();
        this.refreshToken = tokenPairDto.getRefreshToken();
        LOGGER.info("Code successfully exchanged");
    }

    private void refreshAccessToken() {

        LOGGER.info("Refreshing tokens...");

        if (this.refreshToken == null) {
            throw new RuntimeException("Refresh token is null");
        }

        final String json = "{\"refreshToken\":\"%s\"}".formatted(this.refreshToken);
        final HttpResponse<String> response = requestService.sendRequest(
                yoshkaApiConfig.getUrls().getRefreshUrl(), HttpMethod.POST, HttpContentType.JSON, null, json
        );

        if (response.statusCode() == 401) {
            LOGGER.warn("Refresh token expired. Need to login.");
            login();
            return;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Refresh token request failed with status: " + response.statusCode());
        }

        final TokenPairDto tokenPairDto = getTokenPairFromBody(response.body());
        if (tokenPairDto.getAccessToken() == null || tokenPairDto.getRefreshToken() == null) {
            throw new RuntimeException("Invalid token response received during refresh.");
        }

        this.accessToken = tokenPairDto.getAccessToken();
        this.refreshToken = tokenPairDto.getRefreshToken();
        LOGGER.info("Token successfully refreshed");
    }

    private void login() {

        LOGGER.info("Login start for {}", yoshkaApiConfig.getUsername());
        requestService.clearCookie();

        final HttpResponse<String> responseLoginPage = requestService.sendRequest(
                yoshkaApiConfig.getUrls().getLoginPageUrl(), HttpMethod.GET, HttpContentType.JSON, null, null
        );
        if (responseLoginPage.statusCode() != 200) {
            throw new RuntimeException("Login page request failed with status: " + responseLoginPage.statusCode());
        }

        final String loginFormUrl = getLoginFormUrlFromBody(responseLoginPage.body());
        if (loginFormUrl == null) {
            throw new RuntimeException("Authorization form not found.");
        }

        final String authForm = "username=" + yoshkaApiConfig.getUsername() + "&password=" + yoshkaApiConfig.getPassword();
        final HttpResponse<String> responseAuth = requestService.sendRequest(
                loginFormUrl, HttpMethod.POST, HttpContentType.FORM, null, authForm
        );
        if (responseAuth.statusCode() != 302 && responseAuth.statusCode() != 303) {
            throw new RuntimeException("Authentication failed, unexpected status: " + responseAuth.statusCode() + ". Check username and password.");
        }

        final String location = responseAuth.headers().firstValue("location").orElse(null);
        if (location == null) {
            throw new RuntimeException("Location header not found.");
        }

        final String code = getCodeFromUrl(location);
        if (code == null) {
            throw new RuntimeException("Authorization code not found");
        }

        exchangeCodeOnToken(code);
        LOGGER.info("Login complete for {}", yoshkaApiConfig.getUsername());


    }

}
