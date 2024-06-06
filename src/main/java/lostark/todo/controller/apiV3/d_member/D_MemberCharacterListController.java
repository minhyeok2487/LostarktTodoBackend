package lostark.todo.controller.apiV3.d_member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.characterDto.CharacterListResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v3/member/characters")
@Api(tags = {"회원 캐릭터 리스트"})
public class D_MemberCharacterListController {

    private final CharacterService characterService;

    @ApiOperation(value = "회원 캐릭터 리스트 조회 - 서버별 분리(Map)",
            notes = "key = 서버 이름, value = 캐릭터 리스트",
            response = CharacterListResponseDto.class)
    @GetMapping()
    public ResponseEntity<?> findCharacterList(@AuthenticationPrincipal String username) {

        // username -> characterList 조회
        List<Character> characterList = characterService.findCharacterListUsername(username);
        if (characterList.isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        // 결과
        Map<String, List<CharacterDto>> characterDtoMap = characterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterDto().toDtoV2(character))
                .sorted(Comparator
                        .comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                .collect(Collectors.groupingBy(CharacterDto::getServerName));
        return new ResponseEntity<>(new CharacterListResponseDto(characterDtoMap), HttpStatus.OK);
    }
}
