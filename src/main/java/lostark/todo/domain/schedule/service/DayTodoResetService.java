package lostark.todo.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.character.repository.CustomTodoRepository;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.repository.ContentRepository;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.domain.servertodo.repository.ServerTodoStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DayTodoResetService {

    private final CharacterRepository characterRepository;
    private final CustomTodoRepository customTodoRepository;
    private final ContentRepository contentRepository;
    private final MarketService marketService;
    private final ServerTodoStateRepository serverTodoStateRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long updateDayContentGauge() {
        return characterRepository.updateDayContentGauge();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long saveBeforeGauge() {
        return characterRepository.saveBeforeGauge();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long updateDayContentCheck() {
        return characterRepository.updateDayContentCheck();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateDayTodoGold() {
        Map<String, Market> contentResource = marketService.findLevelUpResource();

        contentRepository.findAllByDayContent().stream()
                .filter(dayContent -> dayContent.getCategory() == Category.가디언토벌)
                .forEach(dayContent -> {
                    double price = calculateGuardianPrice(dayContent, contentResource);
                    characterRepository.updateDayContentPriceGuardian(dayContent, price);
                });
    }

    private double calculateGuardianPrice(DayContent dayContent, Map<String, Market> contentResource) {
        Market jewelry = getJewelry(dayContent.getLevel(), contentResource);
        double price = jewelry.getRecentPrice() * dayContent.getJewelry();
        return Math.round(price * 100.0) / 100.0;
    }

    private Market getJewelry(double itemLevel, Map<String, Market> contentResource) {
        if (itemLevel >= 1415 && itemLevel < 1640) {
            return contentResource.get("3티어 1레벨 보석");
        } else {
            return contentResource.get("4티어 1레벨 보석");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long updateCustomDailyTodo() {
        return customTodoRepository.update(CustomTodoFrequencyEnum.DAILY);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long resetServerTodoState() {
        return serverTodoStateRepository.resetAllChecked();
    }
}
