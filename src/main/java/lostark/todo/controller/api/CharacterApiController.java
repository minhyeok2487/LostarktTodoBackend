package lostark.todo.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtos.CharacterSaveDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CharacterApiController {

    private final CharacterService characterService;

    @GetMapping("/character/list/{username}")
    public ResponseEntity characterList(@PathVariable String username) {
        try {
            List<Character> characterList = characterService.characterListByUsernameAndSelect(username);
            return new ResponseEntity<>(characterList, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/character/save")
    public ResponseEntity characterSave(CharacterSaveDto characterSaveDto) {
        Character character = characterService.saveCharacter(characterSaveDto);
        return new ResponseEntity<>(character, HttpStatus.OK);
    }
}
