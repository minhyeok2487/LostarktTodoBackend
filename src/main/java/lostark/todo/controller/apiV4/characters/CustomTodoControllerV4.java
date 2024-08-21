package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CheckCustomTodoRequest;
import lostark.todo.controller.dtoV2.character.CreateCustomTodoRequest;
import lostark.todo.controller.dtoV2.character.CustomTodoResponse;
import lostark.todo.controller.dtoV2.character.UpdateCustomTodoRequest;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.customTodo.CustomTodo;
import lostark.todo.service.CharacterService;
import lostark.todo.service.CustomTodoService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/custom")
@Api(tags = {"커스텀 숙제 API"})
public class CustomTodoControllerV4 {

    private final CharacterService characterService;
    private final CustomTodoService customTodoService;
    private final MemberService memberService;

    @ApiOperation(value = "커스텀 숙제 조회")
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username) {
        List<CustomTodo> search = customTodoService.search(username);
        List<CustomTodoResponse> response = search.stream().map(CustomTodoResponse::new).toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 추가")
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateCustomTodoRequest request) {
        Character character = characterService.get(request.getCharacterId(), username);
        customTodoService.create(character, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 수정")
    @PatchMapping("/{customTodoId}")
    public ResponseEntity<?> update(@AuthenticationPrincipal String username,
                                    @RequestBody UpdateCustomTodoRequest request,
                                    @PathVariable Long customTodoId) {
        Character character = characterService.get(request.getCharacterId(), username);
        customTodoService.update(character, request, customTodoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 체크")
    @PostMapping("/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal String username,
                                    @RequestBody CheckCustomTodoRequest request) {
        Character character = characterService.get(request.getCharacterId(), username);
        customTodoService.check(character, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 삭제")
    @DeleteMapping("/{customTodoId}")
    public ResponseEntity<?> remove(@AuthenticationPrincipal String username, @PathVariable Long customTodoId) {
        List<Long> characterIdList = memberService.get(username).getCharacters().stream().map(Character::getId).toList();
        customTodoService.remove(characterIdList, customTodoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
