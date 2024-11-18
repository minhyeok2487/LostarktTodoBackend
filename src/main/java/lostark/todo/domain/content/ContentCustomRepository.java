package lostark.todo.domain.content;

import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;

import java.util.List;
import java.util.Map;

public interface ContentCustomRepository {

    List<RaidCategoryResponse> getScheduleRaidCategory();

    List<WeekContent> findAllWeekContent(double itemLevel);

    Map<Category, List<DayContent>> getDayContents();
}
