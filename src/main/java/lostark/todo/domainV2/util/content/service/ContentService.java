package lostark.todo.domainV2.util.content.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;
import lostark.todo.domain.content.*;
import lostark.todo.domainV2.util.content.dao.ContentDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentDao contentDao;

    public Content findById(long id) {
        return contentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("없는 컨텐츠 입니다."));
    }

    public List<Content> findAllByIdWeekContent(List<Long> idList) {
        return contentDao.findAllByIdWeekContent(idList);
    }

    // 카테고리(카오스던전, 가디언토벌)별 일일컨텐츠 출력
    public List<DayContent> findDayContent(Category category) {
        return contentDao.findDayContent(category);
    }

    /**
     * 아이템 레벨보다 작은 주간 컨텐츠 조회
     */
    public List<WeekContent> findAllWeekContent(double itemLevel) {
        List<WeekContent> allWeekContent = contentRepository.findAllWeekContent(itemLevel);
        if (allWeekContent.isEmpty()) {
            throw new IllegalStateException("컨텐츠 불러오기 오류");
        }
        return allWeekContent;
    }

    public List<WeekContent> findAllByCategoryAndWeekCategory(double itemLevel, String weekCategory, WeekContentCategory weekContentCategory) {
        return contentRepository.findAllWeekContent(itemLevel, weekCategory, weekContentCategory);
    }

    @Transactional(readOnly = true)
    public List<RaidCategoryResponse> getScheduleRaidCategory() {
        return contentRepository.getScheduleRaidCategory();
    }
}
