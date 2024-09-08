package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;
import lostark.todo.domain.content.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;

    public Content findById(long id) {
        return contentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("없는 컨텐츠 입니다."));
    }

    public List<Content> findAllByIdWeekContent(List<Long> idList) {
        return contentRepository.findAllById(idList);
    }

    /**
     *  카테고리(카오스던전, 가디언토벌)별 일일컨텐츠 출력
     */
    public List<DayContent> findDayContent(Category category) {
        if(category.equals(Category.가디언토벌) || category.equals(Category.카오스던전)) {
            return contentRepository.findDayContentByCategoryOrderByLevelDesc(category);
        } else {
            throw new IllegalArgumentException("카테고리가 일일컨텐츠가 아닙니다.(카오스던전, 가디언토벌)");
        }
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

    /**
     * 큐브 컨텐츠 호출
     */
    public CubeContent findCubeContent(String name) {
        return contentRepository.findByName(name).orElseThrow(()->new IllegalArgumentException(name + "은 없는 컨텐츠 입니다."));
    }

    @Transactional(readOnly = true)
    public List<RaidCategoryResponse> getScheduleRaidCategory() {
        return contentRepository.getScheduleRaidCategory();
    }

    @Transactional(readOnly = true)
    public List<CubeContent> findAllCubeContent() {
        return contentRepository.findAllByCubeContent();
    }
}
