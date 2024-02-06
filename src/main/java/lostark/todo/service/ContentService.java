package lostark.todo.service;

import lombok.RequiredArgsConstructor;
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

    /**
     *  카테고리(카오스던전, 가디언토벌)별 일일컨텐츠 출력
     */
    public List<DayContent> findDayContent(Category category) {
        if(category.equals(Category.가디언토벌) || category.equals(Category.카오스던전)) {
            List<DayContent> dayContentList = contentRepository.findDayContentByCategoryOrderByLevelDesc(category);
            return dayContentList;
        } else {
            throw new IllegalArgumentException("카테고리가 일일컨텐츠가 아닙니다.(카오스던전, 가디언토벌)");
        }
    }

    /**
     * 일일 컨텐츠 이름으로 조회
     */
    public DayContent findDayContentByName(String name) {
        return (DayContent) contentRepository.findContentByName(name)
                .orElseThrow(() -> new IllegalArgumentException(name+" - 없는 컨텐츠 입니다."));
    }

    /**
     * 주간 컨텐츠 전체 find
     */
    public List<WeekContent> findAllByWeekContent() {
        return contentRepository.findAllByWeekContent();
    }

    /**
     * 주간 컨텐츠 추가
     */
    public WeekContent saveWeekContent(WeekContent weekContent) {
        Category weekCategory = weekContent.getCategory();
        if(weekCategory.equals(Category.군단장레이드) ||
                weekCategory.equals(Category.어비스던전) ||
                weekCategory.equals(Category.어비스레이드)) {
            return contentRepository.save(weekContent);
        } else {
            throw new IllegalArgumentException("카테고리가 주간 컨텐츠가 아닙니다.");
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

    public List<WeekContent> findAllByWeekContentWithItemLevel(double itemLevel) {
        return contentRepository.findAllByWeekContentWithItemLevel(itemLevel);
    }
    public List<WeekContent> findAllByWeekContentWithItemLevelV2(double itemLevel) {
        return contentRepository.findAllByWeekContentWithItemLevelV2(itemLevel);
    }



    public List<WeekContent> findAllByCategoryAndWeekCategory(double itemLevel, String weekCategory, WeekContentCategory weekContentCategory) {
        return contentRepository.findAllWeekContent(itemLevel, weekCategory, weekContentCategory);
    }

    public WeekContent findByWeekContent(String weekCategory, int currentGate) {
        return contentRepository.findByWeekCategoryAndGate(weekCategory, currentGate)
                .orElseThrow(() -> new IllegalArgumentException(""));
    }

    /**
     * 큐브 컨텐츠 호출
     */
    public CubeContent findCubeContent(String name) {
        return contentRepository.findByName(name).orElseThrow(()->new IllegalArgumentException(name + "은 없는 컨텐츠 입니다."));
    }
}
