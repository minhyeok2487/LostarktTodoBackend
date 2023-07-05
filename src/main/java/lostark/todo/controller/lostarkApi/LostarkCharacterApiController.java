package lostark.todo.controller.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtos.MemberFindDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/lostarkApi/character")
public class LostarkCharacterApiController {

    private final LostarkCharacterService lostarkCharacterService;

    @PostMapping("/{characterName}")
    public List<Character> characterInfo(@RequestBody MemberFindDto memberFindDto, @PathVariable String characterName) throws Exception {
        Member member = lostarkCharacterService.characterInfoAndSave(memberFindDto.getUsername(), characterName);
        return member.getCharacters();
    }


}
