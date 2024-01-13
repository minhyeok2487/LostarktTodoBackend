package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.*;
import lostark.todo.service.lostarkApi.LostarkApiService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v2/member")
@Api(tags = {"회원 API V2"})
public class MemberApiControllerV2 {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;
    private final LostarkCharacterService lostarkCharacterService;
    private final LostarkApiService lostarkApiService;
    private final LogsService logsService;
    private final ConcurrentHashMap<String, Boolean> usernameLocks;

    @ApiOperation(value = "회원 캐릭터 리스트 조회 - 서버별 분리(Map)",
            notes = "key = 서버 이름, value = 캐릭터 리스트",
            response = Map.class)
    @GetMapping("/characterList")
    public ResponseEntity<?> findCharacterList(@AuthenticationPrincipal String username) {

        // username -> characterList 조회
        List<Character> characterList = characterService.findCharacterListUsername(username);

        // 결과
        Map<String, List<CharacterDto>> characterDtoMap = characterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterDto().toDtoV2(character))
                .sorted(Comparator
                        .comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                .collect(Collectors.groupingBy(CharacterDto::getServerName));
        return new ResponseEntity<>(characterDtoMap, HttpStatus.OK);
    }
}
