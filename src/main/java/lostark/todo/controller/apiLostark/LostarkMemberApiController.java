package lostark.todo.controller.apiLostark;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.MemberFindDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.lostarkApi.LostarkMemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/lostark/character")
public class LostarkMemberApiController {
    /**
     * 로스트아크 api와 Character DB가 연동된 api
     */

    private final LostarkMemberService lostarkMemberService;

    /**
     * 로스트아크 api로부터 캐릭터 정보 불러와서 DB에 저장
     */
    @PostMapping("/{characterName}")
    public List<Character> characterInfo(@RequestBody MemberFindDto memberFindDto, @PathVariable String characterName) {
        Member member = lostarkMemberService.characterInfoAndSave(memberFindDto.getUsername(), characterName);
        return member.getCharacters();
    }


}
