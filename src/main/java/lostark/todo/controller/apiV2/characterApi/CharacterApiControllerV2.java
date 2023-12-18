package lostark.todo.controller.apiV2.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v2/character")
@Api(tags = {"캐릭터 API V2"})
public class CharacterApiControllerV2 {

    private final CharacterService characterService;
    private final MemberService memberService;

    @ApiOperation(value = "골드 획득 캐릭터 지정/해제", response = CharacterDto.class)
    @PatchMapping("/gold-character")
    public ResponseEntity<CharacterDto> updateGoldCharacter(@AuthenticationPrincipal String username, @RequestBody CharacterDefaultDto characterDefaultDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                characterDefaultDto.getCharacterId(), characterDefaultDto.getCharacterName(), username);

        // 골드 획득 캐릭터 지정
        Character resultCharacter = characterService.updateGoldCharacter(character);

        return new ResponseEntity<>(new CharacterDto().toDtoV2(resultCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "원정대 주간 숙제(도전어비스, 도전가디언) 수정",
            response = CharacterDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT Bearer 토큰", required = true,
                    dataTypeClass = String.class, paramType = "header")
    })
    @PatchMapping("/challenge")
    public ResponseEntity updateChallenge(@AuthenticationPrincipal String username,
                                          @RequestBody CharacterChallengeRequestDto characterChallengeRequestDto) {
        // username -> member 조회
        Member member = memberService.findMember(username);

        // 도전 어비스, 가디언 업데이트
        List<Character> characterList = characterService.updateChallenge(
                member, characterChallengeRequestDto.getServerName(), characterChallengeRequestDto.getContent());

        return new ResponseEntity<>(new CharacterDto().toDtoList(characterList), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 출력 내용 수정")
    @PatchMapping("/settings")
    public ResponseEntity<CharacterDto> updateSettings(@AuthenticationPrincipal String username, @RequestBody SettingRequestDto settingRequestDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                settingRequestDto.getCharacterId(), settingRequestDto.getCharacterName(), username);

        characterService.updateSetting(character, settingRequestDto.getName(), settingRequestDto.isValue());

        return new ResponseEntity<>(new CharacterDto().toDtoV2(character), HttpStatus.OK);
    }
}
