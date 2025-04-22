package lostark.todo.domain.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.entity.RaidBusGold;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.content.enums.WeekContentCategory;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeekContentResponse {
    private long id;

    private String weekCategory;

    private WeekContentCategory weekContentCategory;

    private String name;

    private double level;

    private int gate; //관문

    private int gold; //골드

    private boolean moreRewardCheck; //더보기 체크

    private int busGold; //버스 골드

    private boolean checked; //선택

    private int coolTime; //주기

    private boolean goldCheck; //골드 획득

    private boolean busGoldFixed; // 버스 골드 고정 여부

    public WeekContentResponse toDto(WeekContent weekContent) {
        return WeekContentResponse.builder()
                .id(weekContent.getId())
                .weekCategory(weekContent.getWeekCategory())
                .weekContentCategory(weekContent.getWeekContentCategory())
                .level(weekContent.getLevel())
                .checked(false)
                .gate(weekContent.getGate())
                .gold(weekContent.getGold())
                .moreRewardCheck(false)
                .busGold(0)
                .name(weekContent.getName())
                .coolTime(weekContent.getCoolTime())
                .goldCheck(false)
                .build();
    }

    public void updateBusGold(RaidBusGold raidBusGold) {
        this.busGold = raidBusGold.getBusGold();
        this.busGoldFixed =raidBusGold.isFixed();
    }
}
