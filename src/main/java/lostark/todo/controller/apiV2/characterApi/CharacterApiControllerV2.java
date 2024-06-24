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

    // TODO 추후 삭제
    @ApiOperation(value = "골드 획득 캐릭터 지정/해제", response = CharacterDto.class)
    @PatchMapping("/gold-character")
    public ResponseEntity<CharacterDto> updateGoldCharacter(@AuthenticationPrincipal String username,
                                                            @RequestBody CharacterDefaultDto characterDefaultDto) {
        Character character = characterService.findCharacter(
                characterDefaultDto.getCharacterId(), characterDefaultDto.getCharacterName(), username);

        // 골드 획득 캐릭터 지정
        Character resultCharacter = characterService.updateGoldCharacter(character);

        return new ResponseEntity<>(new CharacterDto().toDtoV2(resultCharacter), HttpStatus.OK);
    }

    // TODO 추후 삭제
    @ApiOperation(value = "캐릭터 출력 내용 수정")
    @PatchMapping("/settings")
    public ResponseEntity<?> updateSettings(@AuthenticationPrincipal String username, @RequestBody SettingRequestDto settingRequestDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                settingRequestDto.getCharacterId(), settingRequestDto.getCharacterName(), username);

        characterService.updateSetting(character, settingRequestDto.getName(), settingRequestDto.isValue());

        return new ResponseEntity<>(CharacterSettingDto.toDto(character), HttpStatus.OK);
    }
}
