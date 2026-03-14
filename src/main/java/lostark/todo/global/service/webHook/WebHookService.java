package lostark.todo.global.service.webHook;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WebHookService {

    private static final List<String> WEBHOOK_EXCLUDE_KEYWORDS = List.of(
            "점검중",
            "올바르지 않은 apiKey"
    );

    @Value("${discord.webhookURL}")
    private String url;

    private final RestTemplate restTemplate;
    private final Cache<String, Boolean> cooldownCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public WebHookService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    public void sendMessage(DiscordWebhook.EmbedObject embedObject, String url) {
        DiscordWebhook webhook = new DiscordWebhook(url);
        webhook.setUsername("Loatodo 알림");
        webhook.addEmbed(embedObject);
        try {
            webhook.execute();
        } catch (IOException e) {
            log.warn("Discord webhook 전송 실패: {}", e.getMessage());
        }
    }

    @Async("taskExecutor")
    public void callEvent(Exception ex, String requestInfo) {
        try {
            if (!shouldSendNotification(ex)) {
                return;
            }

            String exceptionKey = ex.getClass().getSimpleName();
            JSONObject data = new JSONObject();
            String message = "```" + exceptionKey + "발생";
            message += "\n";
            message += ex.getMessage();
            message += "\n";
            message += requestInfo;
            message += "```";
            data.put("content", message);
            send(data, url);
        } catch (Exception e) {
            log.warn("Discord webhook 전송 실패: {}", e.getMessage());
        }
    }

    boolean shouldSendNotification(Exception ex) {
        String errorMessage = ex.getMessage();
        if (errorMessage != null && WEBHOOK_EXCLUDE_KEYWORDS.stream().anyMatch(errorMessage::contains)) {
            log.warn("Webhook 제외 대상: {}", errorMessage);
            return false;
        }

        String exceptionKey = ex.getClass().getSimpleName();
        if (cooldownCache.getIfPresent(exceptionKey) != null) {
            log.debug("Webhook 쿨다운 중 ({}), 전송 생략", exceptionKey);
            return false;
        }
        cooldownCache.put(exceptionKey, Boolean.TRUE);
        return true;
    }

    private void send(JSONObject object, String webhookUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(object.toString(), headers);
        restTemplate.postForObject(webhookUrl, entity, String.class);
    }
}
