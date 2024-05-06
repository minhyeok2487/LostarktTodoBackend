package lostark.todo.controller.apiV4.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.firend.FriendsResponse;
import lostark.todo.domain.member.Member;
import lostark.todo.service.FriendsService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends")
@Api(tags = {"깐부 리스트"})
public class FriendsControllerV4 {

    private final FriendsService friendsService;
    private final MemberService memberService;

    @ApiOperation(value = "깐부 리스트 조회 API",
            response = FriendsResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        if (member.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        List<FriendsResponse> friends = friendsService.getFriendList(member);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }
}
