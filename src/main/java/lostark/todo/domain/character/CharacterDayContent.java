package lostark.todo.domain.character;

import lombok.AllArgsConstructor;
import lombok.Data;
import lostark.todo.controller.v1.dto.characterDto.CharacterDayContentResponseDtoV1;
import lostark.todo.controller.dto.characterDto.CharacterUpdateDto;
import lostark.todo.domain.content.Category;

import javax.persistence.Embeddable;
@Embeddable
@Data
@AllArgsConstructor
public class CharacterDayContent {

    private boolean chaosSelected;

    private int chaosCheck; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지(0~100)

    private boolean guardianSelected;

    private int guardianCheck; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    private int guardianGauge; //가디언토벌 휴식게이지(0~100)

    /**
     * 기본 생성자
     */
    public CharacterDayContent() {
        this.chaosSelected = true;
        this.chaosCheck = 0;
        this.chaosGauge = 0;
        this.guardianSelected = true;
        this.guardianCheck = 0;
        this.guardianGauge = 0;
    }

    /**
     * V2 : 일일컨텐츠 업데이트 메서드
     */
    public void updateDayContent(CharacterUpdateDto characterUpdateDto) {
        this.chaosSelected = characterUpdateDto.getChaosSelected();
        this.guardianSelected = characterUpdateDto.getGuardianSelected();
        this.chaosCheck = characterUpdateDto.getChaosCheck();
        this.guardianCheck = characterUpdateDto.getGuardianCheck();
    }

    /**
     * CharacterContent 값 변경 메소드
     */
    public void update(CharacterDayContentResponseDtoV1 characterDayContentResponseDtoV1) {
        updateChecked(characterDayContentResponseDtoV1);
        updateGauge(characterDayContentResponseDtoV1);
    }

    /**
     * 체크 값 변경 메소드
     * 0, 1, 2 만 가능
     */
    public void updateChecked(CharacterDayContentResponseDtoV1 characterDayContentResponseDtoV1) {
        int chaos = characterDayContentResponseDtoV1.getChaos();
        if (chaos > 2 || chaos < 0) {
            throw new IllegalArgumentException("카오스던전 체크 범위 초과(0~2)");
        } else {
            this.chaosCheck = chaos;
        }

        int guardian = characterDayContentResponseDtoV1.getGuardian();
        if (guardian > 2 || guardian < 0) {
            throw new IllegalArgumentException("가디언토벌 체크 범위 초과(0~2)");
        } else {
            this.guardianCheck = guardian;
        }
    }


    /**
     * 휴식게이지 값 변경 메소드
     * 0~100, 10단위로만 가능
     */
    public void updateGauge(CharacterDayContentResponseDtoV1 characterDayContentResponseDtoV1) {
        int chaosGauge = characterDayContentResponseDtoV1.getChaosGauge();
        if(chaosGauge % 10 == 0 && chaosGauge <= 100 && chaosGauge >= 0) {
            this.chaosGauge = chaosGauge;
        } else {
            throw new IllegalArgumentException("카오스던전 휴식게이지 범위 초과(0~100, 10단위)");
        }

        int guardianGauge = characterDayContentResponseDtoV1.getGuardianGauge();
        if(guardianGauge % 10 == 0 && guardianGauge <= 100 && guardianGauge >= 0) {
            this.guardianGauge = guardianGauge;
        } else {
            throw new IllegalArgumentException("카오스던전 휴식게이지 범위 초과(0~100, 10단위)");
        }
    }

    public void changeCount(Category category) {
        if (category.equals(Category.카오스던전)) {
            if(this.chaosCheck == 2) {
                this.chaosCheck = 0;
            } else if(this.chaosCheck <= 1) {
                this.chaosCheck = 2;
            }
        }
        if (category.equals(Category.가디언토벌)) {
            if(this.guardianCheck == 2) {
                this.guardianCheck = 0;
            } else if(this.guardianCheck <= 1) {
                this.guardianCheck = 2;
            }
        }
    }

    public void calculateChaos(int result) {
        this.chaosGauge = result;
        this.chaosCheck = 0;
    }

    public void calculateGuardian(int result) {
        this.guardianGauge = result;
        this.guardianCheck = 0;
    }

    public void changeSelected(Category category) {
        if (category.equals(Category.카오스던전)) {
            this.chaosSelected = !this.isChaosSelected();
        }

        if (category.equals(Category.가디언토벌)) {
            this.guardianSelected = !this.isGuardianSelected();
        }
    }


}
