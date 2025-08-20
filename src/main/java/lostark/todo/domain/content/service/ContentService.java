package lostark.todo.domain.content.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.AddContentRequest;
import lostark.todo.domain.character.dto.WeekContentResponse;
import lostark.todo.domain.content.entity.CubeContent;
import lostark.todo.domain.schedule.dto.RaidCategoryResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.content.entity.Content;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.repository.ContentRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.cache.annotation.CacheEvict;
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

    @CacheEvict(cacheNames = "content", allEntries = true)
    public Content addContent(AddContentRequest request) {
        Content content;
        switch (request.getContentType()) {
            case "day":
                content = DayContent.builder()
                        .name(request.getName())
                        .level(request.getLevel())
                        .category(request.getCategory())
                        .shilling(request.getShilling())
                        .honorShard(request.getHonorShard())
                        .leapStone(request.getLeapStone())
                        .destructionStone(request.getDestructionStone())
                        .guardianStone(request.getGuardianStone())
                        .jewelry(request.getJewelry())
                        .build();
                break;
            case "week":
                content = WeekContent.builder()
                        .name(request.getName())
                        .level(request.getLevel())
                        .category(request.getCategory())
                        .weekCategory(request.getWeekCategory())
                        .weekContentCategory(request.getWeekContentCategory())
                        .gate(request.getGate())
                        .gold(request.getGold())
                        .characterGold(request.getCharacterGold())
                        .coolTime(request.getCoolTime())
                        .moreRewardGold(request.getMoreRewardGold())
                        .honorShard(request.getHonorShard())
                        .leapStone(request.getLeapStone())
                        .destructionStone(request.getDestructionStone())
                        .guardianStone(request.getGuardianStone())
                        .build();
                break;
            case "cube":
                content = CubeContent.builder()
                        .name(request.getName())
                        .level(request.getLevel())
                        .category(request.getCategory())
                        .jewelry(request.getJewelry())
                        .leapStone(request.getLeapStone())
                        .solarGrace(request.getSolarGrace())
                        .solarBlessing(request.getSolarBlessing())
                        .solarProtection(request.getSolarProtection())
                        .cardExp(request.getCardExp())
                        .lavasBreath(request.getLavasBreath())
                        .glaciersBreath(request.getGlaciersBreath())
                        .shilling(request.getShilling())
                        .build();
                break;
            default:
                throw new ConditionNotMetException("Invalid content type: " + request.getContentType());
        }
        return contentRepository.save(content);
    }
}
