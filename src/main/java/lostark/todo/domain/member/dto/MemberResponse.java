package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.entity.Member;

import javax.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static lostark.todo.global.Constant.TEST_USERNAME;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
    @NotEmpty
    @ApiModelProperty(example = "회원 id")
    private long memberId;

    @NotEmpty
    @ApiModelProperty(example = "회원 이름")
    private String username;

    @NotEmpty
    @ApiModelProperty(example = "대표 캐릭터")
    private MainCharacterResponse mainCharacter;

    @NotEmpty
    @ApiModelProperty(example = "권한")
    private Role role;

    @NotEmpty
    @ApiModelProperty(example = "광고 제거 계졍")
    private boolean ads;

    @NotEmpty
    @ApiModelProperty(example = "광고 날짜")
    private LocalDateTime adsDate;

    private List<LifeEnergyResponse> lifeEnergyResponses;

    public static MemberResponse toDto(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .username(createUsername(member))
                .mainCharacter(createMainCharacter(member))
                .role(member.getRole())
                .ads(isAds(member))
                .adsDate(member.getAdsDate())
                .lifeEnergyResponses(LifeEnergyResponse.toDto(member.getLifeEnergyList()))
                .build();
    }

    private static String createUsername(Member member) {
        return Optional.of(member.getUsername())
                .filter(username -> !username.equals(TEST_USERNAME))
                .orElse(null);
    }

    private static MainCharacterResponse createMainCharacter(Member member) {
        return member.getCharacters().stream()
                .filter(character -> character.getCharacterName().equals(member.getMainCharacterName()))
                .findFirst()
                .map(MainCharacterResponse::new)
                .orElseGet(MainCharacterResponse::new);
    }

    private static boolean isAds(Member member) {
        if (member.getAdsDate() == null) {
            return false;
        } else {
            return member.getAdsDate().isAfter(LocalDateTime.now());
        }
    }
}
