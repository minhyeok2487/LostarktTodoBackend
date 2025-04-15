package lostark.todo.domain.util.content.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dtoV2.content.RaidCategoryResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.util.content.entity.Content;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.util.content.entity.WeekContent;
import lostark.todo.domain.util.content.enums.Category;
import lostark.todo.domain.util.content.repository.ContentRepository;
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

    public List<WeekContentDto> getTodoForm(Character updateCharacter) {
        List<WeekContent> allWeekContent = contentRepository.findAllWeekContent(updateCharacter.getItemLevel());
        if (allWeekContent.isEmpty()) {
            throw new ConditionNotMetException("아이템레벨: " + updateCharacter.getItemLevel() + "보다 작은 레이드가 없습니다.");
        }

        return allWeekContent.stream()
                .map(weekContent -> {
                    WeekContentDto weekContentDto = new WeekContentDto().toDto(weekContent);
                    updateCharacter.getTodoV2List().stream()
                            .filter(todo -> todo.getWeekContent().equals(weekContent))
                            .findFirst()
                            .ifPresent(todo -> {
                                weekContentDto.setChecked(true);
                                weekContentDto.setGoldCheck(todo.isGoldCheck());
                            });
                    updateCharacter.getRaidBusGoldList().stream()
                            .filter(raidBusGold -> raidBusGold.getWeekCategory().equals(weekContent.getWeekCategory()))
                            .findFirst()
                            .ifPresent(weekContentDto::updateBusGold);
                    return weekContentDto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "content", key = "'dayContent'")
    public Map<Category, List<DayContent>> getDayContent() {
        return contentRepository.getDayContents();
    }
}
