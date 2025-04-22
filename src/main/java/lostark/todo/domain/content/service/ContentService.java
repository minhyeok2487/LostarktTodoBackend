package lostark.todo.domain.content.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.dto.WeekContentResponse;
import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.content.entity.Content;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.repository.ContentRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.cache.annotation.Cacheable;
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

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "content", key = "'raid-category'")
    public List<RaidCategoryResponse> getScheduleRaidCategory() {
        return contentRepository.getScheduleRaidCategory();
    }

    public List<WeekContentResponse> getTodoForm(Character updateCharacter) {
        List<WeekContent> allWeekContent = contentRepository.findAllWeekContent(updateCharacter.getItemLevel());
        if (allWeekContent.isEmpty()) {
            throw new ConditionNotMetException("아이템레벨: " + updateCharacter.getItemLevel() + "보다 작은 레이드가 없습니다.");
        }

        return allWeekContent.stream()
                .map(weekContent -> {
                    WeekContentResponse weekContentResponse = new WeekContentResponse().toDto(weekContent);
                    updateCharacter.getTodoV2List().stream()
                            .filter(todo -> todo.getWeekContent().equals(weekContent))
                            .findFirst()
                            .ifPresent(todo -> {
                                weekContentResponse.setChecked(true);
                                weekContentResponse.setGoldCheck(todo.isGoldCheck());
                            });
                    updateCharacter.getRaidBusGoldList().stream()
                            .filter(raidBusGold -> raidBusGold.getWeekCategory().equals(weekContent.getWeekCategory()))
                            .findFirst()
                            .ifPresent(weekContentResponse::updateBusGold);
                    return weekContentResponse;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "content", key = "'dayContent'")
    public Map<Category, List<DayContent>> getDayContent() {
        return contentRepository.getDayContents();
    }
}
