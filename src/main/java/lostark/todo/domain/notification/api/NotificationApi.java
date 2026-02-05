package lostark.todo.domain.notification.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.notification.dto.NotificationStatusResponse;
import lostark.todo.domain.notification.dto.SearchNotificationResponse;
import lostark.todo.domain.board.community.entity.Community;
import lostark.todo.domain.board.community.service.CommunityService;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.domain.notification.service.NotificationService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/notification")
@Api(tags = {"알림 API"})
public class NotificationApi {

    private final NotificationService notificationService;
    private final MemberService memberService;
    private final CommunityService communityService;

    @ApiOperation(value = "알림 조회 API", response = SearchNotificationResponse.class)
    @GetMapping()
    public ResponseEntity<List<SearchNotificationResponse>> search(@AuthenticationPrincipal String username) {
        // 멤버 정보 가져오기
        Member member = memberService.get(username);

        List<Community> boards;
        List<Notification> notifications;

        if (!member.getRole().equals(Role.ADMIN)) {
            // 공지사항 검색
            boards = communityService.searchBoards();

            // 알림 검색
            notifications = notificationService.search(member, boards);
        } else {
            notifications = notificationService.search(member);
        }

        // 알림을 응답 객체로 변환
        List<SearchNotificationResponse> result = notifications.stream().map(notification -> {
            JSONObject object = new JSONObject();

            // 알림 타입에 따라 NotificationData 생성
            switch (notification.getNotificationType()) {
                case BOARD -> {
                    object.put("boardId", notification.getBoardId());
                }
                case FRIEND -> {
                    object.put("friendId", notification.getFriendId());
                    object.put("friendUsername", notification.getFriendUsername());
                    object.put("friendCharacterName", notification.getFriendCharacterName());
                }
                case COMMUNITY -> {
                    object.put("communityId", notification.getCommunityId());
                }
                case INSPECTION -> {
                    object.put("inspectionCharacterId", notification.getInspectionCharacterId());
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
