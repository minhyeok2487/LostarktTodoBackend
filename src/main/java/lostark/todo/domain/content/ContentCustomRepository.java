package lostark.todo.domain.content;

import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;

import java.util.List;

public interface ContentCustomRepository {

    List<RaidCategoryResponse> getScheduleRaidCategory();
}
