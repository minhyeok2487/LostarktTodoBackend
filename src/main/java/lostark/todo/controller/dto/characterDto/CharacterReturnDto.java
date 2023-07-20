package lostark.todo.controller.dto.characterDto;

import lombok.Data;
import lostark.todo.domain.character.Character;

@Data
public class CharacterReturnDto {

    private long id;

    private String characterClassName;

    private String characterName;

    private double itemLevel; //아이템레벨

    private boolean selected;

    private boolean chaosSelected;

    private String chaosName;

    private int chaosCheck; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지

    private double chaosProfit;

    private boolean guardianSelected;

    private String guardianName;

    private int guardianCheck; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    private int guardianGauge; //가디언토벌 휴식게이지

    private double guardianProfit;

    public CharacterReturnDto(Character character) {
        this.id = character.getId();
        this.characterClassName = character.getCharacterClassName();
        this.characterName = character.getCharacterName();
        this.itemLevel = character.getItemLevel();
        this.selected = character.isSelected();
        this.chaosSelected = character.getCharacterDayContent().isChaosSelected();
        this.chaosCheck = character.getCharacterDayContent().getChaosCheck();
        this.chaosGauge = character.getCharacterDayContent().getChaosGauge();
        this.guardianSelected = character.getCharacterDayContent().isGuardianSelected();
        this.guardianCheck = character.getCharacterDayContent().getGuardianCheck();
        this.guardianGauge = character.getCharacterDayContent().getGuardianGauge();
    }

    public void calculateChaos(String chaosName, double chaosProfit) {
        this.chaosName = chaosName;
        this.chaosProfit = chaosProfit;
    }

}
