package lostark.todo.controller.api.characterApi;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
@Api(tags = {"캐릭터 API"})
public class CharacterApiController {

//    private final CharacterService characterService;
//    private final MemberService memberService;
//
//    @ApiOperation(value = "골드 획득 캐릭터 지정")
//    @PostMapping("/gold-character/{characterName}")
//    public ResponseEntity updateGoldCharacter(@AuthenticationPrincipal String username,
//                                              @PathVariable("characterName") String characterName) {
//        // 로그인한 아이디에 등록된 캐릭터인지 검증
//        // 다른 아이디면 자동으로 Exception 처리
//        Character character = characterService.findCharacterWithMember(characterName, username);
//
//        // 골드 획득 캐릭터 지정
//        Character resultCharacter = characterService.updateGoldCharacter(character);
//
//        return new ResponseEntity<>(new CharacterDto().toDtoV2(resultCharacter), HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "원정대 주간 숙제(도전어비스, 도전가디언) 수정")
//    @PatchMapping("/challenge")
//    public ResponseEntity updateChallenge(@AuthenticationPrincipal String username,
//                                            @RequestBody CharacterChallengeRequestDto dto) {
//        // username -> member 조회
//        Member member = memberService.findMember(username);
//        List<Character> characters = characterService.updateChallenge(member, dto.getServerName(), dto.getContent());
//        return new ResponseEntity<>(new CharacterChallengeResponseDto().toDto(characters.get(0)), HttpStatus.OK);
//    }
//
//    @PatchMapping("/settings")
//    public ResponseEntity updateSettings(@AuthenticationPrincipal String username, @RequestBody SettingRequestDto dto) {
//        Character character = characterService.findCharacterWithMember(dto.getCharacterName(), username);
//        characterService.updateSetting(character, dto.getName(), dto.isValue());
//        return new ResponseEntity(CharacterSettingDto.toDto(character), HttpStatus.OK);
//    }
}
