package lostark.todo.controller.dto;

import lombok.Data;
import lostark.todo.domain.character.Character;

@Data
public class CharacterReturnDto {

    private long id;

    private String characterClassName;

    private String characterName;

    private double itemLevel; //아이템레벨

    private String chaosName;

    private int chaos; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지

    private double chaosProfit;

    private String guardianName;

    private int guardian; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    private int guardianGauge; //가디언토벌 휴식게이지

    private double guardianProfit;

    public CharacterReturnDto(Character character) {
        this.id = character.getId();
        this.characterClassName = character.getCharacterClassName();
        this.characterName = character.getCharacterName();
        this.itemLevel = character.getItemLevel();
        this.chaos = character.getChaos();
        this.chaosGauge = character.getChaosGauge();
        this.guardian = character.getGuardian();
        this.guardianGauge = character.getGuardianGauge();
    }

    public void calculateChaos(String chaosName, double chaosProfit) {
        this.chaosName = chaosName;
        this.chaosProfit = chaosProfit;
    }
}
