package lostark.todo.domain.character;

import lombok.*;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.domain.content.DayContent;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Embeddable;
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

    @Size(max = 200)
    private int chaosGauge; //카오스던전 휴식게이지(0~200)

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

    @ColumnDefault("0")
    private double weekTotalGold; // 이번주 일일 숙제 수익

    @Size(max = 100)
    private int beforeEponaGauge; //이전 에포나 휴식게이지(0~100)

    @Size(max = 100)
    private int beforeChaosGauge; //이전 카오스던전 휴식게이지(0~100)

    @Size(max = 100)
    private int beforeGuardianGauge; //이전 가디언토벌 휴식게이지(0~100)

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

    public DayTodo createDayContent(List<DayContent> chaos, List<DayContent> guardian, double itemLevel) {
        this.chaosName = chaos.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get().getName();
        this.chaos = chaos.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get();
        this.guardianName = guardian.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get().getName();
        this.guardian = guardian.stream().filter(dayContent -> dayContent.getLevel() <= itemLevel).findFirst().get();
        return this;
    }

    private void addWeekTotalGold(double gold) {
        this.weekTotalGold += gold;
    }

    private void subsWeekTotalGold(double gold) {
        this.weekTotalGold -= gold;
        if (this.weekTotalGold < 0) {
            this.weekTotalGold = 0;
        }
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
            eponaGauge = beforeEponaGauge;
        }
    }

    public void updateCheckEponaAll() {
        if(eponaCheck2 == 3) {
            eponaCheck2 = 0;
            eponaGauge = beforeEponaGauge;
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
        double gold = calculateChaosGold();

        if (chaosCheck < 2) {
            chaosCheck += 1;
            if (chaosGauge >= 20) {
                chaosGauge -= 20;
            }
            addWeekTotalGold(gold);
        } else {
            resetChaos();
        }
    }

    public void updateCheckChaosAll() {
        if (chaosCheck != 2) {
            while (chaosCheck < 2) {
                updateCheckChaos();
            }
        } else {
            resetChaos();
        }
    }

    private double calculateChaosGold() {
        double gold;
        if (beforeChaosGauge >= 20 && beforeChaosGauge < 40) {
            if (chaosGauge >= 20 && chaosGauge < 40) {
                gold = this.chaosGold * 2 / 3;
            } else {
                gold = this.chaosGold / 3;
            }
        } else {
            gold = this.chaosGold / 2;
        }
        return gold;
    }

    private void resetChaos() {
        chaosCheck = 0;
        subsWeekTotalGold(this.chaosGold);
        chaosGauge = beforeChaosGauge;
    }


    public void updateCheckGuardian() {
        if(guardianCheck < 1) {
            guardianCheck += 1;
            if(guardianGauge >= 20) {
                guardianGauge -= 20;
            }
            addWeekTotalGold(this.guardianGold);
        } else {
            guardianCheck = 0;
            if (beforeGuardianGauge >= 20) {
                if (guardianGauge >= 20) {
                    subsWeekTotalGold(this.guardianGold);
                } else {
                    subsWeekTotalGold(this.guardianGold * 2);
                }
            } else {
                subsWeekTotalGold(this.guardianGold);
            }
            guardianGauge = beforeGuardianGauge;
        }
    }

    public void updateDayContentGauge(CharacterDayTodoDto characterDayTodoDto) {
        this.chaosGauge = characterDayTodoDto.getChaosGauge();
        this.beforeChaosGauge = chaosGauge;
        this.guardianGauge = characterDayTodoDto.getGuardianGauge();
        this.beforeGuardianGauge = guardianGauge;
        this.eponaGauge = characterDayTodoDto.getEponaGauge();
        this.beforeEponaGauge = eponaGauge;
    }
}
