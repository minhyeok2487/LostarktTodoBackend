package lostark.todo.controller.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.Market;
import lostark.todo.service.MarketService;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    private final CharacterService characterService;
    private final ContentService contentService;
    private final MarketService marketService;

    // TestController
//    @GetMapping("/")
//    public String index() {
//        return "index";
//    }

    @GetMapping("/db/characters")
    public String dbCharacters(Model model) {
        model.addAttribute("characterList", characterService.findAll());
        return "admin/dbCharacters";
    }

    @GetMapping("/db/content")
    public String dbContent(Model model) {
        model.addAttribute("contentList", contentService.findAllDayContent());
        return "admin/dbContent";
    }

    @GetMapping("/db/market")
    public String dbMarket(Model model) {
        // 재련재료 데이터 리스트 호출
        List<String> resource = marketService.dayContentResource();

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        List<Market> marketList = marketService.findByNameIn(resource);

        // 업데이트 시간
        LocalDateTime lastModifiedDate = marketList.get(4).getLastModifiedDate();
        String format = lastModifiedDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
        model.addAttribute("marketList", marketList);
        model.addAttribute("lastModifiedDate", format);
        return "admin/dbMarket";
    }
}
