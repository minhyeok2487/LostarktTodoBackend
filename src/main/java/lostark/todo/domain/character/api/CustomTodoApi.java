package lostark.todo.domain.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.service.CustomTodoService;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.friendPermisson.CharacterMemberQueryService;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
        return new ResponseEntity<>(customTodoService.searchResonse(member.getUsername()), HttpStatus.OK);
    }
}
