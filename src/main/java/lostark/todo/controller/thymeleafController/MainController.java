package lostark.todo.controller.thymeleafController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;

    @GetMapping("/")
    public String main() {
        return "main";
    }

    // 숙제 관리 화면
    @GetMapping("/todo/{username}")
    public String todo(@PathVariable String username, Model model) {
        try {
            // header : username으로 연결된 캐릭터리스트 중 선택할 리스트 가져옴
            List<Character> characterList = memberService.findMemberSelected(username).getCharacters();

            // 거래소 데이터 가져옴(Map)
            Map<String , MarketContentResourceDto> contentResource = marketService.getContentResource(makeDayContentResourceNames());

            // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
            List<CharacterReturnDto> characterReturnDtoList = contentService.calculateDayContent(characterList, contentResource);

            // 모두 더하기
            double sum = 0;
            for (CharacterReturnDto returnDto : characterReturnDtoList) {
                sum += returnDto.getChaosProfit();
                sum += returnDto.getGuardianProfit();
            }
            model.addAttribute("characterList", characterReturnDtoList);
            model.addAttribute("sum", Math.round(sum * 100.0) / 100.0);
            return "todo/todoMain";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
