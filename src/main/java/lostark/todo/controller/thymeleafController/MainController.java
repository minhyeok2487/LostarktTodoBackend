package lostark.todo.controller.thymeleafController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
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
            // 캐릭터 리스트 가져옴
            List<Character> characterList = memberService.readCharacterList(username);

            // 거래소 데이터 가져옴(Map)
            Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(marketService.dayContentResource());

            // ItemLevel이 1415이상인 캐릭터는 레벨에 맞는 일일 컨텐츠 가져온후 계산
            List<CharacterReturnDto> characterReturnDtoList = contentService.calculateDayContent(characterList, contentResource);

            // 일일숙제 선택된 캐릭터들
            // Profit 순서대로 정렬하기
            JSONArray sortedDayContentProfit = contentService.sortDayContentProfit(characterReturnDtoList);

            // Profit 합 구하기
            double sum = 0;
            for (Object o : sortedDayContentProfit) {
                JSONObject jsonObject = (JSONObject) o;
                double profit = (double) jsonObject.get("profit");
                sum += profit;
            }
            sum = Math.round(sum * 100.0) / 100.0;

            // 결과 출력
            model.addAttribute("characters", characterReturnDtoList);
            model.addAttribute("sumDayContentProfit", sum);
            model.addAttribute("sortDayContentProfit", sortedDayContentProfit);

            return "todo/todoMain";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
