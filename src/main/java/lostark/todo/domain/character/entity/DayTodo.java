package lostark.todo.domain.character.entity;

import lombok.*;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.character.dto.UpdateDayGaugeRequest;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.market.entity.Market;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

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

    @Size(max = 3)
    private int eponaCheck2;

    @Size(max = 100)
    private int eponaGauge;

    @ColumnDefault("0")
    private double weekTotalGold; // 이번주 일일 숙제 수익

    @Size(max = 100)
    private int beforeEponaGauge; //이전 에포나 휴식게이지(0~100)

    @Size(max = 200)
    private int beforeChaosGauge; //이전 카오스던전 휴식게이지(0~200)

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

    public DayTodo createDayContent(Map<Category, List<DayContent>> dayContents, double itemLevel) {
        this.chaos = findContentByLevel(dayContents.get(Category.카오스던전), itemLevel);
        this.guardian = findContentByLevel(dayContents.get(Category.가디언토벌), itemLevel);
        this.chaosName = this.chaos.getName();
        this.guardianName = this.guardian.getName();
        return this;
    }

    private DayContent findContentByLevel(List<DayContent> contents, double itemLevel) {
        return contents.stream()
                .filter(content -> content.getLevel() <= itemLevel)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No content found for item level: " + itemLevel));
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
        if (eponaCheck2 < 3) {
            eponaCheck2 += 1;
            if (eponaGauge >= 20) {
                eponaGauge -= 20;
            }
        } else {
            eponaCheck2 = 0;
            eponaGauge = beforeEponaGauge;
        }
    }

    public void updateCheckEponaAll() {
        if (eponaCheck2 == 3) {
            eponaCheck2 = 0;
            eponaGauge = beforeEponaGauge;
        } else {
            while (eponaCheck2 < 3) {
                eponaCheck2 += 1;
                if (eponaGauge >= 20) {
                    eponaGauge -= 20;
                }
            }
        }
    }

    public void updateCheckChaos() {
        double gold = calculateChaosGold();
        if (chaosCheck != 2) {
            chaosCheck = 2;
            if (chaosGauge >= 40 && beforeChaosGauge == chaosGauge) { // 체크된 상태로 전체 체크시 휴식게이지 차감 방지
                chaosGauge -= 40;
            }
            addWeekTotalGold(gold);
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
        if (guardianCheck < 1) {
            guardianCheck += 1;
            if (guardianGauge >= 20 && beforeGuardianGauge == guardianGauge) { // 체크된 상태로 전체 체크시 휴식게이지 차감 방지
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

    // 휴식 게이지 관련
    public void updateDayContentGauge(UpdateDayGaugeRequest request) {
        this.chaosGauge = request.getChaosGauge();
        this.beforeChaosGauge = chaosGauge;
        this.guardianGauge = request.getGuardianGauge();
        this.beforeGuardianGauge = guardianGauge;
    }

    public void calculateDayTodo(Character character, Map<String, Market> contentResource) {
        this.calculateChaos();

        Market jewelry = getJewelry(character.getItemLevel(), contentResource);
        DayContent guardian = character.getDayTodo().getGuardian();

        this.calculateGuardian(guardian, jewelry);
    }

    private Market getJewelry(double itemLevel, Map<String, Market> contentResource) {
        if (itemLevel >= 1415 && itemLevel < 1640) {
            return contentResource.get("3티어 1레벨 보석");
        } else {
            return contentResource.get("4티어 1레벨 보석");
        }
    }

    private void calculateChaos() {
        // 12/10 업데이트: 카오스 던전 보상이 캐릭터 귀속으로 변경되어 골드 계산 제거
        this.setChaosGold(0);
    }

    private void calculateGuardian(DayContent dayContent, Market jewelry) {
        // 12/10 업데이트: 가디언 토벌에서 보석만 드랍 (파괴석/수호석/돌파석은 캐릭터 귀속)
        double price = jewelry.getRecentPrice() * dayContent.getJewelry();

        int guardianGauge = this.getGuardianGauge();
        if (guardianGauge >= 20) {
            price = price * 2;
        }

        price = Math.round(price * 100.0) / 100.0;
        this.setGuardianGold(price);
    }
}
