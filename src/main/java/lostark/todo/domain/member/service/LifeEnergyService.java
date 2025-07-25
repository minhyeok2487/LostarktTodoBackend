package lostark.todo.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.dto.LifeEnergySaveRequest;
import lostark.todo.domain.member.dto.LifeEnergySpendRequest;
import lostark.todo.domain.member.dto.LifeEnergyUpdateRequest;
import lostark.todo.domain.member.entity.LifeEnergy;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.LifeEnergyRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LifeEnergyService {

    private final LifeEnergyRepository repository;

    @Transactional
    public void save(Member member, LifeEnergySaveRequest request) {
        LifeEnergy lifeEnergy = LifeEnergy.toEntity(member, request);
        repository.save(lifeEnergy);
    }

    @Transactional
    public void update(String username, LifeEnergyUpdateRequest request) {
        LifeEnergy lifeEnergy = get(request.getId(), username);

        lifeEnergy.update(request);
    }

    @Transactional
    public void delete(String username, String characterName) {
        LifeEnergy lifeEnergy = repository.findByMemberUsernameAndCharacterName(username, characterName)
                .orElseThrow(() -> new ConditionNotMetException("해당 캐릭터의 생활의 기운 정보가 존재하지 않습니다."));
        repository.delete(lifeEnergy);
    }

    @Transactional
    public LifeEnergy spend(String username, LifeEnergySpendRequest request) {
        LifeEnergy lifeEnergy = get(request.getId(), username);
        lifeEnergy.spend(request);
        return lifeEnergy;
    }

    private LifeEnergy get(Long request, String username) {
        LifeEnergy lifeEnergy = repository.findById(request)
                .orElseThrow(() -> new ConditionNotMetException("데이터가 존재하지 않습니다."));

        if (!lifeEnergy.getMember().getUsername().equals(username)) {
            throw new ConditionNotMetException("권한이 없습니다.");
        }

        return lifeEnergy;
    }
}
