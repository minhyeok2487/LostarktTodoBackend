package lostark.todo.domain.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CheckCustomTodoRequest;
import lostark.todo.domain.character.dto.CreateCustomTodoRequest;
import lostark.todo.domain.character.dto.UpdateCustomTodoRequest;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CustomTodoService;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.friendPermisson.CharacterMemberQueryService;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/custom")
@Api(tags = {"커스텀 숙제 API"})
public class CustomTodoApi {

    private final CharacterMemberQueryService characterMemberQueryService;
    private final CustomTodoService customTodoService;

    @ApiOperation(value = "커스텀 숙제 조회")
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username,
                                    @RequestParam(required = false) String friendUsername) {
        Member member = characterMemberQueryService.getUpdateMember(username, friendUsername, FriendPermissionType.SHOW);
        return new ResponseEntity<>(customTodoService.searchResponse(member.getUsername()), HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 추가")
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestParam(required = false) String friendUsername,
                                    @RequestBody @Valid CreateCustomTodoRequest request) {
        Character updateCharacter = characterMemberQueryService.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_SETTING);
        customTodoService.create(updateCharacter, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 수정")
    @PatchMapping("/{customTodoId}")
    public ResponseEntity<?> update(@AuthenticationPrincipal String username,
                                    @RequestParam(required = false) String friendUsername,
                                    @RequestBody @Valid UpdateCustomTodoRequest request,
                                    @PathVariable Long customTodoId) {
        Character updateCharacter = characterMemberQueryService.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_SETTING);
        customTodoService.update(updateCharacter, request, customTodoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 체크")
    @PostMapping("/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal String username,
                                   @RequestParam(required = false) String friendUsername,
                                   @RequestBody @Valid CheckCustomTodoRequest request) {
        Character updateCharacter = characterMemberQueryService.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_WEEK_TODO);
        customTodoService.check(updateCharacter, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 삭제")
    @DeleteMapping("/{customTodoId}")
    public ResponseEntity<?> remove(@AuthenticationPrincipal String username,
                                    @RequestParam(required = false) String friendUsername,
                                    @PathVariable Long customTodoId) {
        Member member = characterMemberQueryService.getUpdateMember(username, friendUsername,
                FriendPermissionType.UPDATE_SETTING);
        List<Long> characterIdList = member.getCharacters().stream().map(Character::getId).toList();
        customTodoService.remove(characterIdList, customTodoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
