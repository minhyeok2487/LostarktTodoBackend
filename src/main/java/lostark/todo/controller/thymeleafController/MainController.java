package lostark.todo.controller.thymeleafController;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
public class MainController {

    @GetMapping("/")
    public String main() {
        return "main";
    }

    // 숙제 관리 화면
    @GetMapping("/todo/{username}")
    public String todo(@PathVariable String username) {
        return "todo/todoMain";
    }
}
