package lostark.todo.service;

import lostark.todo.exhandler.exceptions.CustomIllegalArgumentException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebHookService {
    @Value("${discord.webhookURL}")
    private  String url;

    public void callEvent(Exception ex){
        JSONObject data = new JSONObject();
        String message = "```" + ex.getClass().getSimpleName() + "발생";
        message += "\n";
        message += ex.getMessage();
        message += "```";
        data.put("content", message);
        send(data);
    }

    public void callEvent(CustomIllegalArgumentException ex){
        JSONObject data = new JSONObject();
        String message = "``` [" + ex.getMethod()+ "]  발생";
        message += "\n";
        if(ex.getMember() != null) {
            message += ex.getMember().getId() + " / " + ex.getMember().getUsername() + " / ";
        }
        message += ex.getMessage();
        message += "```";
        data.put("content", message);
        send(data);
    }

    private void send(JSONObject object){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(object.toString(), headers);
        restTemplate.postForObject(url, entity, String.class);
    }
}
