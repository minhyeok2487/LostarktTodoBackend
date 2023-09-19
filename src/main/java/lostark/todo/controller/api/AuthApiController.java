package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @ApiOperation(value = "로그아웃", notes = "구글 로그인 로그아웃")
    @GetMapping("/logout")
    public ResponseEntity logout(@AuthenticationPrincipal String username) {
        String accessKey = memberService.findMember(username).getAccessKey();
        try {
            // URL to revoke the token
            String revokeUrl = "https://accounts.google.com/o/oauth2/revoke";

            // Create a connection to the revoke URL
            URL url = new URL(revokeUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the Content-Length header
            String requestBody = "token=" + accessKey; // Include the access token in the request body
            connection.setRequestProperty("Content-Length", String.valueOf(requestBody.length()));

            // Enable input/output streams
            connection.setDoOutput(true);

            // Write the request body
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }

            // Get the response code (HTTP 200 indicates success)
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Close the connection
            connection.disconnect();
            log.info("로그아웃 성공");
            return new ResponseEntity("로그아웃 성공", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
