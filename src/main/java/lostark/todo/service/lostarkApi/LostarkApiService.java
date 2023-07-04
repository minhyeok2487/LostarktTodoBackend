package lostark.todo.service.lostarkApi;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class LostarkApiService {

    /**
     * 로스트아크 api 틀(Get, Post)
     */

    //Get mapping
    public InputStreamReader LostarkGetApi(String link, String apiKey) {
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnection(link, "GET", apiKey);
            return getInputStreamReader(httpURLConnection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Post mapping
    public InputStreamReader LostarkPostApi(String link, String parameter, String apiKey) {
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnection(link, "POST", apiKey);

            byte[] out = parameter.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = httpURLConnection.getOutputStream();
            stream.write(out);

            return getInputStreamReader(httpURLConnection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //URL 연결(헤더)
    private HttpURLConnection getHttpURLConnection(String link, String method, String apiKey) throws IOException {
        URL url = new URL(link);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setRequestProperty("authorization", "Bearer " + apiKey);
        httpURLConnection.setRequestProperty("accept", "application/json");
        httpURLConnection.setRequestProperty("content-Type", "application/json");
        httpURLConnection.setDoOutput(true);
        return httpURLConnection;
    }

    //아웃풋 출력
    private InputStreamReader getInputStreamReader(HttpURLConnection httpURLConnection) throws IOException {
        int result = httpURLConnection.getResponseCode();
        InputStream inputStream;
        if(result == 200) {
            inputStream = httpURLConnection.getInputStream();
        } else {
            inputStream = httpURLConnection.getErrorStream();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return inputStreamReader;
    }
}
