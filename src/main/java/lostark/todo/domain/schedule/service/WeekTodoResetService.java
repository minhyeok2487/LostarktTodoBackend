package lostark.todo.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.character.repository.CustomTodoRepository;
import lostark.todo.domain.character.repository.RaidBusGoldRepository;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.global.keyvalue.KeyValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeekTodoResetService {

    private final KeyValueRepository keyValueRepository;
    private final TodoV2Repository todoV2Repository;
    private final CharacterRepository characterRepository;
    private final CustomTodoRepository customTodoRepository;
    private final RaidBusGoldRepository raidBusGoldRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateTwoCycle() {
        return keyValueRepository.updateTwoCycle();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long resetTodoV2CoolTime2() {
        return todoV2Repository.resetTodoV2CoolTime2();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetTodoV2() {
        todoV2Repository.resetTodoV2();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long updateWeekContent() {
        return characterRepository.updateWeekContent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateWeekDayTodoTotalGold() {
        characterRepository.updateWeekDayTodoTotalGold();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long updateCustomWeeklyTodo() {
        return customTodoRepository.update(CustomTodoFrequencyEnum.WEEKLY);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllRaidBusGold() {
        raidBusGoldRepository.deleteAllRaidBusGold();
    }
}
