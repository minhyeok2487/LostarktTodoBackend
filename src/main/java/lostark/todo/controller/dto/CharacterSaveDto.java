package lostark.todo.controller.dto;

import lombok.Data;

@Data
public class CharacterSaveDto {

    private long id;

    private String characterName;

    private String characterClassName;

    private double itemLevel; //아이템레벨

    private int chaos; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지

    private int guardian; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    private int guardianGauge; //가디언토벌 휴식게이지
}
