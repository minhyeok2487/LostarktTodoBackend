package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecentActivityResponse {

    private String type;           // NEW_MEMBER, NEW_CHARACTER
    private String message;        // "새 회원 가입", "캐릭터 등록"
    private String detail;         // username 또는 "클래스명 아이템레벨"
    private LocalDateTime createdDate;
}
