package lostark.todo.domain.admin.dto;

import lombok.Data;
import lostark.todo.domain.member.enums.Role;

import java.time.LocalDateTime;

@Data
public class AdminMemberUpdateRequest {

    private Role role;
    private String mainCharacter;
    private LocalDateTime adsDate;
}
