package lostark.todo.controller.api;

import io.jsonwebtoken.io.IOException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ApiOperation(value = "로그아웃", notes = "JWT")
    @GetMapping("/logout")
    public ResponseEntity logout(@AuthenticationPrincipal String username) {
        String accessKey = memberService.findMember(username).getAccessKey();
        System.out.println("accessKey: " + accessKey);

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
            return new ResponseEntity("OK", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
