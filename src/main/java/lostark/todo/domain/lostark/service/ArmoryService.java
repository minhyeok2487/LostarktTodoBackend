package lostark.todo.domain.lostark.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArmoryService {

    private final LostarkCharacterApiClient characterApiClient;
    private final MemberService memberService;

    public JsonNode getArmory(String username, String characterName) {
        String apiKey = getApiKey(username);
        return characterApiClient.getFullArmory(characterName, apiKey);
    }

    public JsonNode getSiblings(String username, String characterName) {
        String apiKey = getApiKey(username);
        return characterApiClient.getSiblingsRaw(characterName, apiKey);
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
