package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 인증 (로그인, 회원가입) API
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = {"인증 API"})
public class AuthApiController {

    private final MemberService memberService;

    @ApiOperation(value = "로그아웃", notes = "구글 로그인 로그아웃", response = String.class)
    @GetMapping("/logout")
    public ResponseEntity logout(@AuthenticationPrincipal String username) {
        String accessKey = memberService.findMember(username).getAccessKey();
        try {
            // 토큰 취소 URL
            String revokeUrl = "https://accounts.google.com/o/oauth2/revoke";

            // URl 연결
            URL url = new URL(revokeUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // POST 매핑
            connection.setRequestMethod("POST");

            // Body 생성
            String requestBody = "token=" + accessKey;
            connection.setRequestProperty("Content-Length", String.valueOf(requestBody.length()));

            // input/output 스트림 true
            connection.setDoOutput(true);

            // Body 작성
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }

            // 성공
            int responseCode = connection.getResponseCode();
            log.info("Google Logout Response Code: {}", responseCode);

            // 닫기
            connection.disconnect();
            log.info("Google Logout Success");
            return new ResponseEntity("Google Logout Success", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
