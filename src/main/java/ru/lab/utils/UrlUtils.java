package ru.lab.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.lab.dto.TokenPairDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {

    public static String getCodeFromUrl(final String url) {
        Pattern pattern = Pattern.compile("code=(.+?)(?:&|$)");
        Matcher matcher = pattern.matcher(url);

        String code = null;
        if (matcher.find()) {
            code = matcher.group(1);
        }
        return code;
    }

    public static String getLoginFormUrlFromBody(final String body) {
        final Document document = Jsoup.parse(body);
        final Element form = document.selectFirst("form#kc-form-login");
        if (form == null) {
            return null;
        }
        return form.attr("action");
    }

    public static TokenPairDto getTokenPairFromBody(final String body) {

        try {
            final ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(body);
            String accessToken = json.has("accessToken") ? json.get("accessToken").asText() : null;
            String refreshToken = json.has("refreshToken") ? json.get("refreshToken").asText() : null;
            return new TokenPairDto(accessToken, refreshToken);
        }

        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid token JSON body", e);
        }

    }


}
