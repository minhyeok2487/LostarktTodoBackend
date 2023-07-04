package lostark.todo.controller.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtos.MemberFindDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/lostarkApi")
public class CharacterApiController {

    private final LostarkCharacterService lostarkCharacterService;

    @PostMapping("/character/{characterName}")
    public List<Character> characterInfo(@RequestBody MemberFindDto memberFindDto, @PathVariable String characterName) throws Exception {
        Member member = lostarkCharacterService.characterInfo(memberFindDto.getUsername(), characterName);
        return member.getCharacters();
    }
}
