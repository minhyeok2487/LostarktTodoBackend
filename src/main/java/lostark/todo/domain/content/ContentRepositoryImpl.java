package lostark.todo.domain.content;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.content.QScheduleRaidCategoryResponse;
import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;

import java.util.List;

import static lostark.todo.domain.content.QWeekContent.weekContent;

@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<RaidCategoryResponse> getScheduleRaidCategory() {
        return factory.select(new QScheduleRaidCategoryResponse(
                    weekContent.id, weekContent.name, weekContent.weekContentCategory, weekContent.level
                ))
                .from(weekContent)
                .where(weekContent.weekContentCategory.isNotNull())
                .orderBy(weekContent.level.desc())
                .groupBy(weekContent.name, weekContent.weekContentCategory)
                .fetch();
    }
}
