package lostark.todo.global.service.webHook;

import lostark.todo.global.exhandler.exceptions.CustomIllegalArgumentException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class WebHookService {

    @Value("${discord.webhookURL}")
    private String url;

    public void sendMessage(DiscordWebhook.EmbedObject embedObject, String url) {
        DiscordWebhook webhook = new DiscordWebhook(url);
        webhook.setUsername("Loatodo 알림");
        webhook.addEmbed(embedObject);
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void callEvent(Exception ex) {
        JSONObject data = new JSONObject();
        String message = "```" + ex.getClass().getSimpleName() + "발생";
        message += "\n";
        message += ex.getMessage();
        message += "```";
        data.put("content", message);
        send(data, url);
    }

    public void callEvent(CustomIllegalArgumentException ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            System.out.println("stackTraceElement = " + stackTraceElement);
        }
        JSONObject data = new JSONObject();
        String message = "``` [" + ex.getMethod() + "]  발생";
        message += "\n";
        if (ex.getMember() != null) {
            message += ex.getMember().getId() + " / " + ex.getMember().getUsername() + " / ";
        }
        message += ex.getMessage();
        message += "```";
        data.put("content", message);
        send(data, url);
    }

    private void send(JSONObject object, String webhookUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(object.toString(), headers);
        restTemplate.postForObject(webhookUrl, entity, String.class);
    }
}
