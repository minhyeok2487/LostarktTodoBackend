package lostark.todo.global.service.webHook;

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
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebHookService {

    private static final long COOLDOWN_MILLIS = 5 * 60 * 1000; // 5분

    @Value("${discord.webhookURL}")
    private String url;

    private final RestTemplate restTemplate;
    private final ConcurrentHashMap<String, Long> lastSentTime = new ConcurrentHashMap<>();

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
            String exceptionKey = ex.getClass().getSimpleName();
            long now = System.currentTimeMillis();
            Long lastSent = lastSentTime.get(exceptionKey);

            if (lastSent != null && (now - lastSent) < COOLDOWN_MILLIS) {
                log.debug("Webhook 쿨다운 중 ({}), 전송 생략", exceptionKey);
                return;
            }
            lastSentTime.put(exceptionKey, now);

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

    private void send(JSONObject object, String webhookUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(object.toString(), headers);
        restTemplate.postForObject(webhookUrl, entity, String.class);
    }
}
