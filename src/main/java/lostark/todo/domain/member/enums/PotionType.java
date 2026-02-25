package lostark.todo.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PotionType {
    LEAP(0),
    SMALL(1000),
    MEDIUM(3000),
    LARGE(5000);

    private final int recoveryAmount;
}
