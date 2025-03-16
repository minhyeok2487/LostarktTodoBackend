package lostark.todo.domain.lostark.client;

import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class LostarkApiClient {

    public JSONArray findEvents(String apiKey) {
        try {
            String link = "https://developer-lostark.game.onstove.com/news/events";
            InputStreamReader inputStreamReader = lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(inputStreamReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로스트아크 api 틀(Get, Post)
     */
    public InputStreamReader lostarkGetApi(String link, String apiKey) {
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnection(link, "GET", apiKey);
            return getInputStreamReader(httpURLConnection);
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage());
        }
    }

    public InputStreamReader lostarkPostApi(String link, String parameter, String apiKey) {
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnection(link, "POST", apiKey);

            byte[] out = parameter.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = httpURLConnection.getOutputStream();
            stream.write(out);

            return getInputStreamReader(httpURLConnection);
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage());
        }
    }

    private HttpURLConnection getHttpURLConnection(String link, String method, String apiKey) {
        try {
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("authorization", "Bearer " + apiKey);
            httpURLConnection.setRequestProperty("accept", "application/json");
            httpURLConnection.setRequestProperty("content-Type", "application/json");
            httpURLConnection.setDoOutput(true);
            return httpURLConnection;
        } catch (Exception e) {
            throw new RuntimeException("API 연결 중 오류 발생: " + e.getMessage());
        }
    }

    private InputStreamReader getInputStreamReader(HttpURLConnection httpURLConnection) {
        try {
            int result = httpURLConnection.getResponseCode();
            InputStream inputStream;
            if(result == 200) {
                inputStream = httpURLConnection.getInputStream();
                return new InputStreamReader(inputStream);
            }
            else if(result == 401) {
                throw new ConditionNotMetException("올바르지 않은 apiKey 입니다. (크롬 자동 번역을 확인해주세요.)");
            }
            else if(result == 429) {
                throw new ConditionNotMetException("사용한도 (1분에 100개)를 초과했습니다.");
            }
            else if (result == 503) {
                throw new ConditionNotMetException("로스트아크 서버가 점검중 입니다.");
            }
            else {
                throw new ConditionNotMetException("API 응답 오류: " + httpURLConnection.getResponseMessage());
            }
        } catch (Exception e) {
            throw new ConditionNotMetException("API 응답 처리 중 오류 발생: " + e.getMessage());
        }
    }

}
