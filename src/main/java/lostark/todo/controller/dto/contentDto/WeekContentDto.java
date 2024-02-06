package lostark.todo.controller.dto.contentDto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.Content;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.content.WeekContentCategory;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeekContentDto {
    private long id;

    private String weekCategory;

    private WeekContentCategory weekContentCategory;

    private String name;

    private double level;

    private int gate; //관문

    private int gold; //골드

    private boolean checked; //선택

    private int coolTime; //주기

    private boolean goldCheck; //골드 획득

    public WeekContentDto toDto(WeekContent weekContent) {
        return WeekContentDto.builder()
                .id(weekContent.getId())
                .weekCategory(weekContent.getWeekCategory())
                .weekContentCategory(weekContent.getWeekContentCategory())
                .level(weekContent.getLevel())
                .checked(false)
                .gate(weekContent.getGate())
                .gold(weekContent.getGold())
                .name(weekContent.getName())
                .coolTime(weekContent.getCoolTime())
                .goldCheck(false)
                .build();
    }
}
