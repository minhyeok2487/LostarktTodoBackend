package lostark.todo.controller.v2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.Character;
import lostark.todo.service.v2.CharacterServiceV2;
import lostark.todo.service.v2.ContentServiceV2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    private final CharacterServiceV2 characterService;
    private final ContentServiceV2 contentServiceV2;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/db/characters")
    public String dbCharacters(Model model) {
        model.addAttribute("characterList", characterService.findAll());
        return "admin/dbCharacters";
    }

    @GetMapping("/db/content")
    public String dbContent(Model model) {
        model.addAttribute("contentList", contentServiceV2.findAll());
        return "admin/dbContent";
    }
}
