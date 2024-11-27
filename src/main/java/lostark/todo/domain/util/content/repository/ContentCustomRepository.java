package lostark.todo.domain.util.content.repository;

import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.util.content.entity.WeekContent;
import lostark.todo.domain.util.content.enums.Category;

import java.util.List;
import java.util.Map;

public interface ContentCustomRepository {

    List<RaidCategoryResponse> getScheduleRaidCategory();

    List<WeekContent> findAllWeekContent(double itemLevel);

    Map<Category, List<DayContent>> getDayContents();
}
