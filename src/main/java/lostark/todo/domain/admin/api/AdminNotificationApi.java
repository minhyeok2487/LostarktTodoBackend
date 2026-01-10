package lostark.todo.domain.admin.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.BroadcastNotificationRequest;
import lostark.todo.domain.notification.service.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/notifications")
@RequiredArgsConstructor
public class AdminNotificationApi {

    private final NotificationService notificationService;

    @ApiOperation(value = "어드민 알림 목록 조회")
    @GetMapping
    public ResponseEntity<?> getNotificationList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        return new ResponseEntity<>(notificationService.getNotificationsForAdmin(pageRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 전체 공지 발송")
    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody BroadcastNotificationRequest request) {
        int count = notificationService.broadcast(request.getContent());
        return new ResponseEntity<>("알림 발송 완료: " + count + "명", HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 알림 삭제")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteByAdmin(notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
