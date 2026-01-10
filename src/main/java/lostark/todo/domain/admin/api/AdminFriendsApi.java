package lostark.todo.domain.admin.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.friend.service.FriendsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/friends")
@RequiredArgsConstructor
public class AdminFriendsApi {

    private final FriendsService friendsService;

    @ApiOperation(value = "어드민 깐부 목록 조회")
    @GetMapping
    public ResponseEntity<?> getFriendList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        return new ResponseEntity<>(friendsService.getFriendsForAdmin(pageRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 깐부 삭제")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long friendId) {
        friendsService.deleteByAdmin(friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
