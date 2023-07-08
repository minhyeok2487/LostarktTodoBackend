package lostark.todo.controller.apiController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterSaveDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/character")
public class CharacterApiController {

    /**
     * 캐릭터 관련 RestApi
     */
    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;

    /**
     * header : username
     * 회원에 등록된 캐릭터리스트 중 selected = true 리스트 가져옴
     * Market DB에서 일일컨텐츠 수익 계산에 필요한 데이터 가져옴
     * 휴식게이지를 참고하여 일일컨텐츠 수익 계산
     */
    @GetMapping("/selectedList")
    public ResponseEntity characterList(HttpServletRequest request) {
        try {
            // header : username으로 연결된 캐릭터리스트 중 선택할 리스트 가져옴
            List<Character> characterList = memberService.findMemberSelected(request.getHeader("username")).getCharacters();

            // 거래소 데이터 가져옴(Map)
            Map<String , MarketContentResourceDto> contentResource = marketService.getContentResource(makeDayContentResourceNames());

            // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
            List<CharacterReturnDto> characterReturnDtoList = contentService.calculateDayContent(characterList, contentResource);

            return new ResponseEntity<>(characterReturnDtoList, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 캐릭터 데이터 수정
     */
    @PatchMapping()
    public ResponseEntity characterSave(@RequestBody CharacterSaveDto characterSaveDto) {
        System.out.println("characterSaveDto.getCharacterName() = " + characterSaveDto.getCharacterName());
        Character character = characterService.saveCharacter(characterSaveDto);
        Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(makeDayContentResourceNames());
        CharacterReturnDto result = contentService.calculateDayContentOne(character, contentResource);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private static List<String> makeDayContentResourceNames() {
        List<String> dayContentResource = new ArrayList<>();
        dayContentResource.add("정제된 파괴강석");
        dayContentResource.add("정제된 수호강석");
        dayContentResource.add("찬란한 명예의 돌파석");

        dayContentResource.add("파괴강석");
        dayContentResource.add("수호강석");
        dayContentResource.add("경이로운 명예의 돌파석");

        dayContentResource.add("파괴석 결정");
        dayContentResource.add("수호석 결정");
        dayContentResource.add("위대한 명예의 돌파석");
        dayContentResource.add("1레벨");
        return dayContentResource;
    }


}
