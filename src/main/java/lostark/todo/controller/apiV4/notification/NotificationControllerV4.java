package lostark.todo.controller.apiV4.notification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.notification.GetNotificationResponse;
import lostark.todo.controller.dtoV2.notification.SearchNotificationResponse;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notification.Notification;
import lostark.todo.service.BoardsService;
import lostark.todo.service.CommentsService;
import lostark.todo.service.MemberService;
import lostark.todo.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private final CommentsService commentsService;

    @ApiOperation(value = "알림 조회 API", response = SearchNotificationResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);
        List<Boards> searchBoard = boardsService.search();
        List<Notification> notifications = notificationService.searchBoard(member, searchBoard);
        List<SearchNotificationResponse> result = notifications.stream().map(SearchNotificationResponse::new).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "가장 최근 알림 날짜 조회 API", response = LocalDateTime.class)
    @GetMapping("/recent")
    public ResponseEntity<?> getRecent(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(notificationService.getRecent(username), HttpStatus.OK);
    }

    @ApiOperation(value = "알림 확인 API", response = GetNotificationResponse.class)
    @GetMapping("/{notificationId}")
    public ResponseEntity<GetNotificationResponse> get(@AuthenticationPrincipal String username, @PathVariable long notificationId) {
        Notification notification = notificationService.updateRead(notificationId, username);
        GetNotificationResponse result =
                switch (notification.getNotificationType()) {
                    case BOARD -> new GetNotificationResponse().toBoard(notification.getBoardId());
                    case COMMENT -> {
                        int page = commentsService.findCommentPage(notification.getCommentId());
                        yield new GetNotificationResponse().toComment(notification.getCommentId(), page);
                    }
                    case FRIEND -> new GetNotificationResponse().toFriend(notification);
                };
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
