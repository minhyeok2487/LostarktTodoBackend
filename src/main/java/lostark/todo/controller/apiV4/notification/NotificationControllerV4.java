package lostark.todo.controller.apiV4.notification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.notification.NotificationStatusResponse;
import lostark.todo.controller.dtoV2.notification.SearchNotificationResponse;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notification.Notification;
import lostark.todo.service.BoardsService;
import lostark.todo.domainV2.board.comments.service.CommentsService;
import lostark.todo.domainV2.member.service.MemberService;
import lostark.todo.service.NotificationService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<SearchNotificationResponse>> search(@AuthenticationPrincipal String username) {
        // 멤버 정보 가져오기
        Member member = memberService.get(username);

        // 게시물 검색
        List<Boards> searchBoard = boardsService.search();

        // 알림 검색
        List<Notification> notifications = notificationService.search(member, searchBoard);

        // 알림을 응답 객체로 변환
        List<SearchNotificationResponse> result = notifications.stream().map(notification -> {
            JSONObject object = new JSONObject();

            // 알림 타입에 따라 NotificationData 생성
            switch (notification.getNotificationType()) {
                case BOARD -> {
                    object.put("boardId", notification.getBoardId());
                }
                case COMMENT -> {
                    int page = commentsService.findCommentPage(notification.getCommentId());
                    object.put("commentId", notification.getCommentId());
                    object.put("page", page);
                }
                case FRIEND -> {
                    object.put("friendId", notification.getFriendId());
                    object.put("friendUsername", notification.getFriendUsername());
                    object.put("friendCharacterName", notification.getFriendCharacterName());
                }
                default -> throw new IllegalArgumentException("Unknown notification type: " + notification.getNotificationType());
            }

            // SearchNotificationResponse 객체 생성 및 추가
            return new SearchNotificationResponse(notification, object);
        }).toList();

        // 응답 반환
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "가장 최근 알림 날짜 조회 API", response = NotificationStatusResponse.class)
    @GetMapping("/status")
    public ResponseEntity<?> getRecent(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(notificationService.getStatus(username), HttpStatus.OK);
    }

    @ApiOperation(value = "알림 확인 API")
    @PostMapping("/{notificationId}")
    public ResponseEntity<?> updateRead(@AuthenticationPrincipal String username, @PathVariable long notificationId) {
        notificationService.updateRead(notificationId, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "알림 일괄 확인 API")
    @PostMapping("/all")
    public ResponseEntity<?> updateReadAll(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);
        notificationService.updateReadAll(member);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
