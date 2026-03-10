package lostark.todo.domain.content.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeekContentCategory {
    싱글(1, "싱글", "main"),
    노말(2, "노말", "blue"),
    하드(3, "하드", "red"),
    나이트메어(4, "나이트메어", "purple"),
    _1단계(5, "1단계", "main"),
    _2단계(6, "2단계", "blue"),
    _3단계(7, "3단계", "red");

    private final int sortOrder;
    private final String displayName;
    private final String color;
}
