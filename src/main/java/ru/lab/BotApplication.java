package ru.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.lab.config.YoshkaApiConfig;
import ru.lab.config.YoshkaBotConfig;


@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({YoshkaApiConfig.class, YoshkaBotConfig.class})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
