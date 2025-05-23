package lostark.todo.domain.content.repository;

import lostark.todo.domain.schedule.dto.RaidCategoryResponse;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.content.enums.Category;

import java.util.List;
import java.util.Map;

public interface ContentCustomRepository {

    List<RaidCategoryResponse> getScheduleRaidCategory();

    List<WeekContent> findAllWeekContent(double itemLevel);

    Map<Category, List<DayContent>> getDayContents();
}
