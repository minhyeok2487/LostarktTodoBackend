package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.notices.Notices;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LostarkNewsService {

    private final LostarkApiService apiService;

    public JSONArray findEvents(String apiKey) {
        try {
            String link = "https://developer-lostark.game.onstove.com/news/events";
            InputStreamReader inputStreamReader = apiService.lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);
            return jsonArray;
        } catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Notices> getNoticeList(String apiKey) {
        try {
            String link = "https://developer-lostark.game.onstove.com/news/notices";
            InputStreamReader inputStreamReader = apiService.lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);

            List<Notices> noticesList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                Notices notices = makeNotice(object);
                if(notices != null) {
                    noticesList.add(notices);
                }
            }
            List<Notices> sortedList = noticesList.stream()
                    .sorted(Comparator.comparing(Notices::getDate))
                    .collect(Collectors.toList());
            return sortedList;
        } catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Notices makeNotice(JSONObject object) {
        NoticesType noticesType = null;
        if (object.get("Title").toString().contains("업데이트 내역 안내")) {
            noticesType = NoticesType.업데이트;
        } else {
            if (object.get("Type").toString().equals("공지")) {
                noticesType = NoticesType.공지사항;
            }
        }
        if (noticesType != null) {
            Notices notices = Notices.builder()
                    .type(noticesType)
                    .title(object.get("Title").toString())
                    .date(LocalDateTime.parse(object.get("Date").toString()))
                    .link(object.get("Link").toString())
                    .build();
            return notices;
        }
        return null;
    }
}
