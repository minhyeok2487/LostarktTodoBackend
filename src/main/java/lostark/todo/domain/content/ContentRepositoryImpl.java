package lostark.todo.domain.content;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.content.QRaidCategoryResponse;
import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;

import java.util.List;

import static lostark.todo.domain.content.QWeekContent.weekContent;

@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<RaidCategoryResponse> getScheduleRaidCategory() {
        return factory.select(new QRaidCategoryResponse(
                    weekContent.id, weekContent.name, weekContent.weekContentCategory, weekContent.level
                ))
                .from(weekContent)
                .where(weekContent.weekContentCategory.isNotNull())
                .orderBy(weekContent.level.desc())
                .groupBy(weekContent.name, weekContent.weekContentCategory)
                .fetch();
    }

    @Override
    public List<WeekContent> findAllWeekContent(double itemLevel) {
        QWeekContent weekContent = QWeekContent.weekContent;

        NumberExpression<Integer> categoryOrder = new CaseBuilder()
                .when(weekContent.weekContentCategory.eq(WeekContentCategory.싱글)).then(1)
                .when(weekContent.weekContentCategory.eq(WeekContentCategory.노말)).then(2)
                .when(weekContent.weekContentCategory.eq(WeekContentCategory.하드)).then(3)
                .otherwise(4);

        return factory.selectFrom(weekContent)
                .where(weekContent.level.loe(itemLevel)
                        .and(weekContent.coolTime.loe(2)))
                .orderBy(weekContent.level.asc(), categoryOrder.asc(), weekContent.gate.asc())
                .fetch();
    }
}
