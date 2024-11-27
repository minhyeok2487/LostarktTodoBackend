package lostark.todo.domain.util.content.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;
import lostark.todo.domain.util.content.entity.Content;
import lostark.todo.domain.util.content.entity.WeekContent;
import lostark.todo.domain.util.content.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;

    public List<Content> findAllByIdWeekContent(List<Long> idList) {
        return contentRepository.findAllById(idList);
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

    @Transactional(readOnly = true)
    public List<RaidCategoryResponse> getScheduleRaidCategory() {
        return contentRepository.getScheduleRaidCategory();
    }
}
