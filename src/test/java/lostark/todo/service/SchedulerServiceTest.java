package lostark.todo.service;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
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

    @BeforeEach
    void before() {
        todoV2Repository.beforeUpdate();
        characterRepository.beforeUpdate();
    }

//    @Test
//    public void resetDayTodo() {
//        long startTime = System.currentTimeMillis(); // 작업 시작 시간 기록
//
//        // 재련재료 데이터 리스트로 거래소 데이터 호출
//        Map<String, Market> contentResource = marketService.findContentResource();
//
//        // 휴식게이지 계산
//        characterService.findAll().forEach(character -> {
//            character.getDayTodo().calculateEpona(); // 에포나의뢰, 출석체크 초기화
//            character.getDayTodo().calculateChaos(); // 카오스던전 휴식게이지 계산 후 초기화
//            character.getDayTodo().calculateGuardian(); // 가디언토벌 휴식게이지 계산 후 초기화
//            // 반영된 휴식게이지로 일일숙제 예상 수익 계산
//            characterService.calculateDayTodo(character, contentResource);
//        });
//
//        long endTime = System.currentTimeMillis(); // 작업 종료 시간 기록
//        long executionTime = endTime - startTime; // 작업에 걸린 시간 계산
//
//        log.info("reset day content. time: {} ms", executionTime);
//    }

    @Test
    @Rollback(value = false)
    @DisplayName("기존 주간 숙제 초기화")
    void resetWeekTodo() {
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
    @Rollback(value = false)
    @DisplayName("변경된 주간 숙제 초기화(bulk)")
    void resetWeekTodoV2() {
        log.info("updateTodoV2 = {}", todoV2Repository.resetTodoV2CoolTime2()); // 2주기 레이드 처리

        log.info("todoV2Repository.resetTodoV2() = {}", todoV2Repository.resetTodoV2()); // 주간 레이드 초기화

        log.info("updateWeekContent = {}", characterRepository.updateWeekContent()); // 주간 숙제 초기화
    }
}