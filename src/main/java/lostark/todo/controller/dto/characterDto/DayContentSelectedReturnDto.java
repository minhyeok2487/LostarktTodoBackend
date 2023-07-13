package lostark.todo.controller.dto.characterDto;

import lombok.Data;
import lostark.todo.domain.character.Character;

@Data
public class DayContentSelectedReturnDto {

    private String characterName;

    private boolean chaosSelected;

    private int chaosCheck; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지(0~100)

    private boolean guardianSelected;

    private int guardianCheck; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    private int guardianGauge; //가디언토벌 휴식게이지(0~100)

    public DayContentSelectedReturnDto(Character character) {
        this.characterName = character.getCharacterName();
        this.chaosSelected = character.getCharacterContent().isChaosSelected();
        this.chaosCheck = character.getCharacterContent().getChaosCheck();
        this.chaosGauge = character.getCharacterContent().getChaosGauge();
        this.guardianSelected = character.getCharacterContent().isGuardianSelected();
        this.guardianCheck = character.getCharacterContent().getGuardianCheck();
        this.guardianGauge = character.getCharacterContent().getGuardianGauge();
    }

}
