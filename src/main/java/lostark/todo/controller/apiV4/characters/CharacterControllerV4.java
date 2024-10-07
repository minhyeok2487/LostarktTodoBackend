package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.SettingRequestDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.character.UpdateMemoRequest;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/character")
@Api(tags = {"단일 캐릭터 API"})
public class CharacterControllerV4 {

    private final CharacterService characterService;

    @ApiOperation(value = "골드 획득 캐릭터 지정/해제", response = CharacterResponse.class)
    @PatchMapping("/gold-character")
    public ResponseEntity<CharacterResponse> updateGoldCharacter(@AuthenticationPrincipal String username,
                                                            @RequestBody CharacterDefaultDto characterDefaultDto) {
        Character resultCharacter = characterService.updateGoldCharacter(characterDefaultDto, username);
        return new ResponseEntity<>(CharacterResponse.toDto(resultCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 출력 내용 수정")
    @PatchMapping("/settings")
    public ResponseEntity<?> updateSettings(@AuthenticationPrincipal String username, @RequestBody SettingRequestDto settingRequestDto) {
        Character updateCharacter = characterService.updateSetting(username, settingRequestDto);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 메모 업데이트", notes = "기본 값 null / 길이 제한 100 / null 혹은 빈 칸으로 입력시 null로 저장")
    @PostMapping("/memo")
    public ResponseEntity<?> updateMemo(@AuthenticationPrincipal String username, @RequestBody @Valid UpdateMemoRequest updateMemoRequest) {
        Character updateCharacter = characterService.updateMemo(username, updateMemoRequest);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "등록 캐릭터 단일 삭제")
    @DeleteMapping("/{characterId}")
    public ResponseEntity<?> deleteCharacter(@AuthenticationPrincipal String username, @PathVariable Long characterId) {
        characterService.delete(characterId, username);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
