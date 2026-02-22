package lostark.todo.domain.lostark.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.lostark.client.LostarkApiClient;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/armory")
public class ArmoryApi {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final LostarkApiClient lostarkApiClient;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<?> getArmory(
            @AuthenticationPrincipal String username,
            @RequestParam String characterName) {
        String apiKey = getApiKey(username);
        String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName;

        InputStreamReader reader = lostarkApiClient.lostarkGetApi(url, apiKey);
        try {
            JsonNode result = MAPPER.readTree(reader);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Armory 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @GetMapping("/siblings")
    public ResponseEntity<?> getSiblings(
            @AuthenticationPrincipal String username,
            @RequestParam String characterName) {
        String apiKey = getApiKey(username);
        String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String url = "https://developer-lostark.game.onstove.com/characters/" + encodedName + "/siblings";

        InputStreamReader reader = lostarkApiClient.lostarkGetApi(url, apiKey);
        try {
            JsonNode result = MAPPER.readTree(reader);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("원정대 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String getApiKey(String username) {
        Member member = memberService.get(username);
        String apiKey = member.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new ConditionNotMetException("API 키가 등록되지 않았습니다. 마이페이지에서 API 키를 등록해주세요.");
        }
        return apiKey;
    }
}
