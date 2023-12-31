package lostark.todo.domain.character;

import lombok.*;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.domain.content.DayContent;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;
import java.util.List;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DayTodo {

    private String chaosName;

    @OneToOne
    @JoinColumn(name = "chaos_id")
    private DayContent chaos;

    @OneToOne
    @JoinColumn(name = "guardian_id")
    private DayContent guardian;

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

    private boolean eponaCheck;

    @Size(max = 3)
    private int eponaCheck2;

    @Size(max = 100)
    private int eponaGauge;


    @Override
    public String toString() {
        return "DayTodo{" +
                "chaosName='" + chaosName + '\'' +
                ", chaosCheck=" + chaosCheck +
                ", chaosGauge=" + chaosGauge +
                ", chaosGold=" + chaosGold +
                ", guardianName='" + guardianName + '\'' +
                ", guardianCheck=" + guardianCheck +
                ", guardianGauge=" + guardianGauge +
                ", guardianGold=" + guardianGold +
                ", eponaCheck=" + eponaCheck +
                ", eponaCheck2=" + eponaCheck2 +
                ", eponaGauge=" + eponaGauge +
                '}';
    }

    /**
     * 일일컨텐츠 업데이트 메서드
     */
    public void updateDayContent(CharacterCheckDto characterCheckDto) {
        this.chaosCheck = characterCheckDto.getChaosCheck();
        this.guardianCheck = characterCheckDto.getGuardianCheck();
        this.eponaCheck2 = characterCheckDto.getEponaCheck();
    }

    /**
     * 에포나의뢰 휴식게이지 계산 후 초기화
     */
    public void calculateEpona() {
        for (int i = 0; i < eponaCheck2; i++) {
            if (eponaGauge >= 20) {
                eponaGauge = subtract(eponaGauge, 20);
            }
        }

        for (int i = 0; i < 3-eponaCheck2; i++) {
            eponaGauge = add(eponaGauge, 10);
        }

        eponaCheck2 = 0;
    }

    /**
     * 카오스던전 휴식게이지 계산 후 초기화
     */
    public void calculateChaos() {
        for (int i = 0; i < chaosCheck; i++) {
            if (chaosGauge >= 20) {
                chaosGauge = subtract(chaosGauge, 20);
            }
        }

        for (int i = 0; i < 2-chaosCheck; i++) {
            chaosGauge = add(chaosGauge, 10);
        }
        chaosCheck = 0;
    }

    /**
     * 가디언토벌 휴식게이지 계산 후 초기화
     */
    public void calculateGuardian() {
        for (int i = 0; i < guardianCheck; i++) {
            if (guardianGauge >= 20) {
                guardianGauge = subtract(guardianGauge, 20);
            }
        }

        for (int i = 0; i < 1-guardianCheck; i++) {
            guardianGauge = add(guardianGauge, 10);
        }
        this.guardianCheck = 0;
    }

    /**
     * 휴식게이지 업데이트
     */
    public void updateGauge(CharacterDayTodoDto characterDayTodoDto) {
        Integer dtoChaosGauge = characterDayTodoDto.getChaosGauge();
        validateGauge(dtoChaosGauge); //검증
        this.chaosGauge = dtoChaosGauge;

        Integer dtoGuardianGauge = characterDayTodoDto.getGuardianGauge();
        validateGauge(dtoGuardianGauge); //검증
        this.guardianGauge = dtoGuardianGauge;

        Integer dtoEponGauge = characterDayTodoDto.getEponaGauge();
        validateGauge(dtoEponGauge);
        this.eponaGauge = dtoEponGauge;
    }

    /**
     * 휴식게이지 검증
     */
    private void validateGauge(Integer gauge) {
        if (gauge < 0 || gauge > 100 || gauge % 10 != 0) {
            throw new IllegalArgumentException("휴식게이지는 0~100 사이이며, 10단위여야 합니다.");
        }
    }
    public DayTodo createDayContent(List<DayContent> chaos, List<DayContent> guardian, double itemLevel) {
        this.chaosName = chaos.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get().getName();
        this.chaos = chaos.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get();
        this.guardianName = guardian.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get().getName();
        this.guardian = guardian.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get();
        return this;
    }

    /**
     * 캐릭터 DayTodo updateCheck
     */
    public void updateCheckEpona() {
        if(eponaCheck2 < 3) {
            eponaCheck2 += 1;
            if(eponaGauge>=20) {
                eponaGauge -= 20;
            }
        } else {
            eponaCheck2 = 0;
        }
    }

    public void updateCheckEponaAll() {
        if(eponaCheck2 == 3) {
            eponaCheck2 = 0;
        } else {
            while (eponaCheck2 < 3) {
                eponaCheck2 += 1;
                if(eponaGauge>=20) {
                    eponaGauge -= 20;
                }
            }
        }
    }

    public void updateCheckChaos() {
        if(chaosCheck < 2) {
            chaosCheck += 1;
            if(chaosGauge >= 20) {
                chaosGauge -= 20;
            }
        } else {
            chaosCheck = 0;
        }
    }

    public void updateCheckChaosAll() {
        if(chaosCheck == 2) {
            chaosCheck = 0;
        } else {
            while (chaosCheck < 2) {
                chaosCheck += 1;
                if(chaosGauge>=20) {
                    chaosGauge -= 20;
                }
            }
        }
    }

    public void updateCheckGuardian() {
        if(guardianCheck < 1) {
            guardianCheck += 1;
            if(guardianGauge >= 20) {
                guardianGauge -= 20;
            }
        } else {
            guardianCheck = 0;
        }
    }

    public void updateCheckGuardianAll() {
        if(guardianCheck < 1) {
            guardianCheck += 1;
            if(guardianGauge >= 20) {
                guardianGauge -= 20;
            }
        } else {
            guardianCheck = 0;
        }
    }


    public void updateCheck(CharacterDayTodoDto characterDayTodoDto) {
        this.eponaCheck2 = characterDayTodoDto.getEponaCheck();
        this.chaosCheck = characterDayTodoDto.getChaosCheck();
        this.guardianCheck = characterDayTodoDto.getGuardianCheck();
    }

    // 두 숫자 더하기
    // 단, 음수가 되면 0을 리턴하는 메서드
    public int subtract(int a, int b) {
        int result = a - b;
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    // 두 숫자 빼기
    // 단, 100이 넘으면 100을 리턴하는 메서드
    public int add(int a, int b) {
        int result = a + b;
        if (result > 100) {
            result = 100;
        }
        return result;
    }


    public void updateDayContentGauge(CharacterDayTodoDto characterDayTodoDto, double chaosGold, double guardianGold) {
        this.chaosGauge = characterDayTodoDto.getChaosGauge();
        this.guardianGauge = characterDayTodoDto.getGuardianGauge();
        this.eponaGauge = characterDayTodoDto.getEponaGauge();
        this.chaosGold = chaosGold;
        this.guardianGold = guardianGold;
    }
}
