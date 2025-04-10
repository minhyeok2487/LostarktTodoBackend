package lostark.todo.domain.character.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.UpdateWeekRaidBusGold;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.RaidBusGold;
import lostark.todo.domain.character.repository.RaidBusGoldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RaidBusGoldService {

    private final RaidBusGoldRepository raidBusGoldRepository;

    @Transactional
    public void UpdateWeekRaidBusGold(Character updateCharacter, UpdateWeekRaidBusGold request) {
        Optional<RaidBusGold> raidBusGold = raidBusGoldRepository.findByCharacterAndWeekCategory(
                updateCharacter, request.getWeekCategory());

        if (raidBusGold.isPresent()) {
            raidBusGold.get().updateWeekRaidBusGold(request.getBusGold());
        } else {
            RaidBusGold newRaidBusGold = RaidBusGold.builder()
                    .character(updateCharacter)
                    .weekCategory(request.getWeekCategory())
                    .busGold(request.getBusGold())
                    .fixed(request.isFixed())
                    .build();
            raidBusGoldRepository.save(newRaidBusGold);
        }
    }
}