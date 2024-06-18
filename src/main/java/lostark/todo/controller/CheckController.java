package lostark.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheckController {

    @GetMapping("/")
    public String check() {
        return "LostarkTodo server is running, 240618";
    }
}
