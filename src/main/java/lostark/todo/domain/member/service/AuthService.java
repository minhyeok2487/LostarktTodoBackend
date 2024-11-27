package lostark.todo.domain.member.service;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.global.dto.GlobalResponseDto;
import lostark.todo.domain.member.entity.Member;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class AuthService {

    public GlobalResponseDto googleLogout(Member member) throws Exception {

        // 토큰 취소 URL
        String revokeUrl = "https://accounts.google.com/o/oauth2/revoke";

        // URl 연결
        URL url = new URL(revokeUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // POST 매핑
        connection.setRequestMethod("POST");

        // Body 생성
        String requestBody = "token=" + member.getAccessKey();
        connection.setRequestProperty("Content-Length", String.valueOf(requestBody.length()));

        // input/output 스트림 true
        connection.setDoOutput(true);

        // Body 작성
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes("UTF-8"));
        }

        // 닫기
        connection.disconnect();
        log.info("구글 로그아웃 성공");
        return new GlobalResponseDto(true, "구글 로그아웃 성공");
    }
}
