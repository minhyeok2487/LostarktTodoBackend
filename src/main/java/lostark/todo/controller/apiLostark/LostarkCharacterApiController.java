package lostark.todo.controller.apiLostark;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/lostark/character")
public class LostarkCharacterApiController {
    /**
     * 로스트아크 api와 Member, Character DB가 연동된 api
     */
    private final LostarkCharacterService lostarkCharacterService;
    private final MemberService memberService;
    private final CharacterService characterService;

    /**
     * 로스트아크 api로부터 캐릭터 정보 불러와서 DB에 저장
     */
    @PostMapping("/{characterName}")
    public ResponseEntity characterInfo(@RequestBody MemberRequestDto memberRequestDto, @PathVariable String characterName) {
        Member member = memberService.findMember(memberRequestDto.getUsername());
        JSONArray characterList = lostarkCharacterService.characterInfo(member.getApiKey(), characterName);
        List<CharacterReturnDto> result = characterService.saveCharacterList(member, characterList);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 기존 캐릭터 정보 갱신
     */
    @PatchMapping("/{characterName}")
    public ResponseEntity characterInfoPatch(@RequestBody MemberRequestDto memberRequestDto, @PathVariable String characterName) {
        Member member = memberService.findMember(memberRequestDto.getUsername());
        JSONArray characterList = lostarkCharacterService.characterInfo(member.getApiKey(), characterName);
        characterService.updateCharacterList(characterList);
        return new ResponseEntity<>(characterList, HttpStatus.OK);
    }


}
