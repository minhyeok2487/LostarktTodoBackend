package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminMemberDetailResponse {

    private long memberId;
    private String username;
    private String authProvider;
    private String apiKey;
    private String mainCharacter;
    private Role role;
    private LocalDateTime adsDate;
    private LocalDateTime createdDate;
    private List<AdminCharacterSimpleResponse> characters;

    public static AdminMemberDetailResponse from(Member member) {
        return AdminMemberDetailResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .authProvider(member.getAuthProvider())
                .apiKey(member.getApiKey())
                .mainCharacter(member.getMainCharacter())
                .role(member.getRole())
                .adsDate(member.getAdsDate())
                .createdDate(member.getCreatedDate())
                .characters(member.getCharacters().stream()
                        .filter(c -> !c.isDeleted())
                        .map(AdminCharacterSimpleResponse::from)
                        .toList())
                .build();
    }
}
