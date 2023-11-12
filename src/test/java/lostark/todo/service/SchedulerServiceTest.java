package lostark.todo.service;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.todoV2.TodoV2Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
@Slf4j
class SchedulerServiceTest {

    @Autowired
    TodoService todoService;

    @Autowired
    CharacterService characterService;

    @Autowired
    MarketService marketService;

    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    TodoV2Repository todoV2Repository;

    @Autowired
    ContentRepository contentRepository;


    @Test
    public void resetDayTodo() {
        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 휴식게이지 계산
        characterService.findAll().forEach(character -> {
            character.getDayTodo().calculateEpona(); // 에포나의뢰, 출석체크 초기화
            character.getDayTodo().calculateChaos(); // 카오스던전 휴식게이지 계산 후 초기화
            character.getDayTodo().calculateGuardian(); // 가디언토벌 휴식게이지 계산 후 초기화
            // 반영된 휴식게이지로 일일숙제 예상 수익 계산
            characterService.calculateDayTodo(character, contentResource);
        });
    }

    @Test
    public void resetDayTodoV2() {
        Map<String, Market> contentResource = marketService.findContentResource();

        log.info("characterRepository.updateDayContentGauge() = {}",characterRepository.updateDayContentGauge());
        log.info("characterRepository.updateDayContentCheck() = {}",characterRepository.updateDayContentCheck());
        double jewelry = (double) contentResource.get("1레벨").getRecentPrice();
        double 파괴석_결정 = (double) contentResource.get("파괴석 결정").getRecentPrice() / 10;
        double 수호석_결정 = (double) contentResource.get("수호석 결정").getRecentPrice() / 10;
        double 파괴강석 = (double) contentResource.get("파괴강석").getRecentPrice() / 10;
        double 수호강석 = (double) contentResource.get("수호강석").getRecentPrice() / 10;
        double 정제된_파괴강석 = (double) contentResource.get("정제된 파괴강석").getRecentPrice() / 10;
        double 정제된_수호강석 = (double) contentResource.get("정제된 수호강석").getRecentPrice() / 10;

        List<DayContent> allByDayContent = contentRepository.findAllByDayContent();
        Map<DayContent, Double> chaos = new HashMap<>();
        Map<DayContent, Double> guardian = new HashMap<>();
        for (DayContent dayContent : allByDayContent) {
            if (dayContent.getLevel()>=1415 && dayContent.getLevel()<1490) {
                if(dayContent.getCategory().equals(Category.카오스던전)) {
                    double price = jewelry * dayContent.getJewelry() + 파괴석_결정 * dayContent.getDestructionStone() + 수호석_결정 * dayContent.getGuardianStone();
                    price = Math.round(price * 100.0) / 100.0;
                    chaos.put(dayContent, price);
                }
                if(dayContent.getCategory().equals(Category.가디언토벌)) {
                    double price = dayContent.getJewelry() + 파괴석_결정 * dayContent.getDestructionStone() + 수호석_결정 * dayContent.getGuardianStone() + dayContent.getLeapStone() * contentResource.get("위대한 명예의 돌파석").getRecentPrice();
                    price = Math.round(price * 100.0) / 100.0;
                    guardian.put(dayContent, price);
                }
            } else if (dayContent.getLevel()>=1490 && dayContent.getLevel()<1580) {
                if(dayContent.getCategory().equals(Category.카오스던전)) {
                    double price = jewelry * dayContent.getJewelry() + 파괴강석 * dayContent.getDestructionStone() + 수호강석 * dayContent.getGuardianStone();
                    price = Math.round(price * 100.0) / 100.0;
                    chaos.put(dayContent, price);
                }
                if(dayContent.getCategory().equals(Category.가디언토벌)) {
                    double price = dayContent.getJewelry() + 파괴강석 * dayContent.getDestructionStone() + 수호강석 * dayContent.getGuardianStone() + dayContent.getLeapStone() * contentResource.get("경이로운 명예의 돌파석").getRecentPrice();
                    price = Math.round(price * 100.0) / 100.0;
                    guardian.put(dayContent, price);
                }
            } else {
                if(dayContent.getCategory().equals(Category.카오스던전)) {
                    double price = jewelry * dayContent.getJewelry() + 정제된_파괴강석 * dayContent.getDestructionStone() + 정제된_수호강석 * dayContent.getGuardianStone();
                    price = Math.round(price * 100.0) / 100.0;
                    chaos.put(dayContent, price);
                }
                if(dayContent.getCategory().equals(Category.가디언토벌)) {
                    double price = dayContent.getJewelry() + 정제된_파괴강석 * dayContent.getDestructionStone() + 정제된_수호강석 * dayContent.getGuardianStone() + dayContent.getLeapStone() * contentResource.get("찬란한 명예의 돌파석").getRecentPrice();
                    price = Math.round(price * 100.0) / 100.0;
                    guardian.put(dayContent, price);
                }
            }
        }

        chaos.forEach((key, value) -> characterRepository.updateDayContentPriceChaos(key, value));
        guardian.forEach((key, value) -> characterRepository.updateDayContentPriceGuardian(key, value));

    }

    @Test
    @DisplayName("기존 주간 숙제 초기화")
    void resetWeekTodo() {
        todoV2Repository.beforeUpdate();
        characterRepository.beforeUpdate();
        todoService.findAllV2().forEach(todoV2 -> {
            WeekContent weekContent = todoV2.getWeekContent();
            if(weekContent.getCoolTime()==2){
                if(todoV2.getCoolTime()==2) {
                    if(todoV2.isChecked()) {
                        todoV2.setCoolTime(0);
                    } else {
                        todoV2.setCoolTime(1);
                    }
                }
                else {
                    todoV2.setCoolTime(2);
                }
            }
            todoV2.setChecked(false);
        });

        characterService.findAll().forEach(character -> {
            character.setChallengeAbyss(false); //도전 어비스 던전
            character.setChallengeGuardian(false); //도전 가디언 토벌
            character.getWeekTodo().setWeekEpona(0); //주간에포나
            character.getWeekTodo().setSilmaelChange(false); //실마엘 혈석교환
        });
    }

    @Test
    @DisplayName("변경된 주간 숙제 초기화(bulk)")
    void resetWeekTodoV2() {
        todoV2Repository.beforeUpdate();
        characterRepository.beforeUpdate();
        log.info("updateTodoV2 = {}", todoV2Repository.resetTodoV2CoolTime2()); // 2주기 레이드 처리

        log.info("todoV2Repository.resetTodoV2() = {}", todoV2Repository.resetTodoV2()); // 주간 레이드 초기화

        log.info("updateWeekContent = {}", characterRepository.updateWeekContent()); // 주간 숙제 초기화
    }
}