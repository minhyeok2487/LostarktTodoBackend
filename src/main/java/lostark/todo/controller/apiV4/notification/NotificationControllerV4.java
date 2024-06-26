package lostark.todo.controller.apiV4.notification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.notification.NotificationResponse;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notification.Notification;
import lostark.todo.service.BoardsService;
import lostark.todo.service.MemberService;
import lostark.todo.service.NotificationService;
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
@RequestMapping("/v4/notification")
@Api(tags = {"알림 API"})
public class NotificationControllerV4 {

    private final NotificationService notificationService;
    private final BoardsService boardsService;
    private final MemberService memberService;

    @ApiOperation(value = "알림 조회 API", response = NotificationResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);
        List<Boards> searchBoard = boardsService.search();
        List<Notification> notifications = notificationService.searchBoard(member, searchBoard);
        List<NotificationResponse> result = notifications.stream().map(NotificationResponse::new).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
