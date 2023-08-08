package lostark.todo.domain.character;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterUpdateDto;
import lostark.todo.domain.content.Category;

import javax.persistence.Embeddable;
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CharacterDayContent {

    private boolean chaosSelected;

    private int chaosCheck; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지(0~100)

    private boolean guardianSelected;

    private int guardianCheck; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    private int guardianGauge; //가디언토벌 휴식게이지(0~100)

    /**
     * 일일컨텐츠 업데이트 메서드
     */
    public void updateDayContent(CharacterUpdateDto characterUpdateDto) {
        this.chaosSelected = characterUpdateDto.getChaosSelected();
        this.guardianSelected = characterUpdateDto.getGuardianSelected();
        this.chaosCheck = characterUpdateDto.getChaosCheck();
        this.guardianCheck = characterUpdateDto.getGuardianCheck();
    }

    public void calculateChaos(int result) {
        this.chaosGauge = result;
        this.chaosCheck = 0;
    }

    public void calculateGuardian(int result) {
        this.guardianGauge = result;
        this.guardianCheck = 0;
    }

}
