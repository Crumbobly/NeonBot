package ru.lab.config;


import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties(prefix = "yoshka")
@Validated
public final class YoshkaApiConfig {

    @NotBlank
    private final String username;
    @NotBlank
    private final String password;

    private final Urls urls;

    @ConstructorBinding
    public YoshkaApiConfig(String username, String password, Urls urls) {
        this.username = username;
        this.password = password;
        this.urls = urls;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Urls getUrls() { return urls; }

    @Validated
    public static class Urls {

        @NotBlank
        private final String usersUrl;
        @NotBlank
        private final String listUrl;
        @NotBlank
        private final String tokenUrl;
        @NotBlank
        private final String refreshUrl;
        @NotBlank
        private final String loginPageUrl;

        @ConstructorBinding
        public Urls(String usersUrl, String listUrl, String tokenUrl, String refreshUrl, String loginPageUrl) {
            this.usersUrl = usersUrl;
            this.listUrl = listUrl;
            this.tokenUrl = tokenUrl;
            this.refreshUrl = refreshUrl;
            this.loginPageUrl = loginPageUrl;
        }

        public String getUsersUrl() { return usersUrl; }
        public String getListUrl() { return listUrl; }
        public String getTokenUrl() { return tokenUrl; }
        public String getRefreshUrl() { return refreshUrl; }
        public String getLoginPageUrl() { return loginPageUrl; }
    }
}
