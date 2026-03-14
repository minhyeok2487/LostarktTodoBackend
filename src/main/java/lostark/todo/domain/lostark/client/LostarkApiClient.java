package lostark.todo.domain.lostark.client;

import lombok.Getter;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Getter
public class LostarkApiClient {

    @Value("${lostark.api.base-url:https://developer-lostark.game.onstove.com}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public LostarkApiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(factory);
    }

    public JSONArray findEvents(String apiKey) {
        try {
            String link = baseUrl + "/news/events";
            String responseBody = lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로스트아크 api 틀(Get, Post)
     */
    public String lostarkGetApi(String link, String apiKey) {
        try {
            HttpHeaders headers = createHeaders(apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(link, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw handleHttpError(e.getStatusCode(), e.getMessage());
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String lostarkPostApi(String link, String parameter, String apiKey) {
        try {
            HttpHeaders headers = createHeaders(apiKey);
            HttpEntity<String> entity = new HttpEntity<>(parameter, headers);
            ResponseEntity<String> response = restTemplate.exchange(link, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw handleHttpError(e.getStatusCode(), e.getMessage());
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private HttpHeaders createHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        return headers;
    }

    private ConditionNotMetException handleHttpError(HttpStatus status, String message) {
        if (status == HttpStatus.UNAUTHORIZED) {
            return new ConditionNotMetException("올바르지 않은 apiKey 입니다. (크롬 자동 번역을 확인해주세요.)");
        } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return new ConditionNotMetException("사용한도 (1분에 100개)를 초과했습니다.");
        } else if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return new ConditionNotMetException("로스트아크 서버가 점검중 입니다.");
        }
        return new ConditionNotMetException(message);
    }
}
