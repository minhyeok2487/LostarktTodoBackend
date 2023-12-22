package lostark.todo.service;

import lostark.todo.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.service.discordWebHook.DiscordWebhook;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.IOException;

@Service
public class WebHookService {

    private String url = "https://discord.com/api/webhooks/1186118216452427776/FkY1W_sn60sZNyxGS5yjTzazuDL6x3op6kNkVeNXKamW1Kp5_Q7BoXdGxoBedcRFY3FQ";

    @Value("${discord.noticeURL}")
    private String noticeUrl;

    public void sendMessage(DiscordWebhook.EmbedObject embedObject) {
        DiscordWebhook webhook = new DiscordWebhook(noticeUrl);
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
