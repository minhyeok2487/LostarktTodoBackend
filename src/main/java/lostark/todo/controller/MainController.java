package lostark.todo.controller;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MainController {

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("member", new Member());
        return "main";
    }

    @GetMapping("/todo")
    public String todo() {
        return "todo/todoMain.html";
    }
}
