package lostark.todo.domain.character;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DayTodo {

    private String chaosName;

    @Size(max = 2)
    private int chaosCheck; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    @Size(max = 100)
    private int chaosGauge; //카오스던전 휴식게이지(0~100)

    private double chaosGold; // 예상골드

    private String guardianName;

    @Size(max = 2)
    private int guardianCheck; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    @Size(max = 100)
    private int guardianGauge; //가디언토벌 휴식게이지(0~100)

    private double guardianGold;

    private boolean eponaCheck; //에포나


    /**
     * 일일컨텐츠 업데이트 메서드
     */
    public void updateDayContent(CharacterCheckDto characterCheckDto) {
        this.chaosCheck = characterCheckDto.getChaosCheck();
        this.guardianCheck = characterCheckDto.getGuardianCheck();
        this.eponaCheck = characterCheckDto.isEponaCheck();
    }

    public void calculateChaos(int result) {
        this.chaosGauge = result;
        this.chaosCheck = 0;
    }

    public void calculateGuardian(int result) {
        this.guardianGauge = result;
        this.guardianCheck = 0;
    }

    public void updateGauge(CharacterDayTodoDto characterDayTodoDto) {
        this.chaosGauge = characterDayTodoDto.getChaosGauge();
        this.guardianGauge = characterDayTodoDto.getGuardianGauge();
    }

    public void createName(double itemLevel) {
        if (itemLevel >= 1415) {
            this.chaosName = "타락1";
            this.guardianName = "데스칼루다";
        }
        if (itemLevel >= 1445) {
            this.chaosName = "타락2";
            this.guardianName = "데스칼루다";
        }
        if (itemLevel >= 1460) {
            this.chaosName = "타락2";
            this.guardianName = "쿤겔라니움";
        }
        if (itemLevel >= 1475) {
            this.chaosName = "타락3";
            this.guardianName = "쿤겔라니움";
        }
        if (itemLevel >= 1490) {
            this.chaosName = "공허1";
            this.guardianName = "칼엘리고스";
        }
        if (itemLevel >= 1520) {
            this.chaosName = "공허2";
            this.guardianName = "칼엘리고스";
        }
        if (itemLevel >= 1540) {
            this.chaosName = "절망1";
            this.guardianName = "하누마탄";
        }
        if (itemLevel >= 1560) {
            this.chaosName = "절망2";
            this.guardianName = "하누마탄";
        }
        if (itemLevel >= 1580) {
            this.chaosName = "천공1";
            this.guardianName = "소나벨";
        }
        if (itemLevel >= 1600) {
            this.chaosName = "천공2";
            this.guardianName = "소나벨";
        }
        if (itemLevel >= 1610) {
            this.chaosName = "계몽1";
            this.guardianName = "가르가디스";
        }
    }

    /**
     * 캐릭터 DayTodo - eponaCheck 변경
     */
    public DayTodo updateCheck(CharacterDayTodoDto characterDayTodoDto) {
        this.eponaCheck = characterDayTodoDto.isEponaCheck();
        this.chaosCheck = characterDayTodoDto.getChaosCheck();
        this.guardianCheck = characterDayTodoDto.getGuardianCheck();
        return this;
    }
}
