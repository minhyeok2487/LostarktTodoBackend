package lostark.todo.domain.cube.enums;

import lombok.Getter;

@Getter
public enum CubeContentName {

    BAN_1("1금제", "ban1"),
    BAN_2("2금제", "ban2"),
    BAN_3("3금제", "ban3"),
    BAN_4("4금제", "ban4"),
    BAN_5("5금제", "ban5"),
    UNLOCK_1("1해금", "unlock1"),
    UNLOCK_2("2해금", "unlock2"),
    UNLOCK_3("3해금", "unlock3");

    private final String name;

    private final String variable;

    CubeContentName(String name, String variable) {
        this.name = name;
        this.variable = variable;
    }
}
