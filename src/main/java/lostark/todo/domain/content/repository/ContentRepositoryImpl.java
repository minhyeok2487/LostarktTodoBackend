package lostark.todo.domain.content.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.schedule.dto.QRaidCategoryResponse;
import lostark.todo.domain.schedule.dto.RaidCategoryResponse;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.content.entity.QDayContent;
import lostark.todo.domain.content.entity.QWeekContent;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.enums.WeekContentCategory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lostark.todo.domain.content.entity.QWeekContent.weekContent;

@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<RaidCategoryResponse> getScheduleRaidCategory() {
        return factory
                .select(new QRaidCategoryResponse(
                        weekContent.id,
                        weekContent.name,
                        weekContent.weekContentCategory,
                        weekContent.level.max()
                ))
                .from(weekContent)
                .where(weekContent.weekContentCategory.isNotNull().and(weekContent.gate.eq(1)))
                .groupBy(weekContent.id, weekContent.name, weekContent.weekContentCategory)
                .orderBy(weekContent.level.max().desc())
                .fetch();
    }


    @Override
    public List<WeekContent> findAllWeekContent(double itemLevel) {
        QWeekContent weekContent = QWeekContent.weekContent;

        NumberExpression<Integer> categoryOrder = buildCategoryOrder(weekContent);

        return factory.selectFrom(weekContent)
                .where(weekContent.level.loe(itemLevel)
                        .and(weekContent.coolTime.loe(2)))
                .orderBy(weekContent.level.desc(), categoryOrder.asc(), weekContent.gate.asc())
                .fetch();
    }

    private NumberExpression<Integer> buildCategoryOrder(QWeekContent weekContent) {
        CaseBuilder caseBuilder = new CaseBuilder();
        CaseBuilder.Cases<Integer, NumberExpression<Integer>> cases = null;
        for (WeekContentCategory cat : WeekContentCategory.values()) {
            if (cases == null) {
                cases = caseBuilder
                        .when(weekContent.weekContentCategory.eq(cat))
                        .then(cat.getSortOrder());
            } else {
                cases = cases
                        .when(weekContent.weekContentCategory.eq(cat))
                        .then(cat.getSortOrder());
            }
        }
        return cases.otherwise(99);
    }

    @Override
    public Map<Category, List<DayContent>> getDayContents() {
        QDayContent dayContent = QDayContent.dayContent;
        List<DayContent> contents = factory.selectFrom(dayContent)
                .where(dayContent.category.in(Category.가디언토벌, Category.카오스던전))
                .orderBy(dayContent.level.desc())
                .fetch();

        return contents.stream()
                .collect(Collectors.groupingBy(DayContent::getCategory));
    }
}
