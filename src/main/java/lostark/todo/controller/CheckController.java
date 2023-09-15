package lostark.todo.controller;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CheckController {

    @GetMapping("/")
    public String check() {
        return "LostarkTodo server is running";
    }
}
