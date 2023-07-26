package lostark.todo.controller.apiLostark;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/lostark/character")
@Api(tags = {"로스트아크 API와 캐릭터 관련 REST API"})
public class LostarkCharacterApiController {

    private final LostarkCharacterService lostarkCharacterService;
    private final MemberService memberService;
    private final CharacterService characterService;

    @ApiOperation(value = "로스트아크 api로부터 캐릭터 정보 불러와서 DB에 저장",
            notes="기존에 있는 캐릭터라면 갱신", response = String.class)
    @PostMapping("/{characterName}")
    public ResponseEntity characterSave(@RequestBody MemberRequestDto memberRequestDto, @PathVariable String characterName) {
        Member member = memberService.findMember(memberRequestDto.getUsername());
        JSONArray characterList = lostarkCharacterService.characterInfo(member.getApiKey(), characterName);
        if (member.getCharacters().size() == 0) {
            return new ResponseEntity<>(characterService.saveCharacterList(member, characterList), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(characterService.updateCharacterList(characterList), HttpStatus.OK);
        }


    }


}
