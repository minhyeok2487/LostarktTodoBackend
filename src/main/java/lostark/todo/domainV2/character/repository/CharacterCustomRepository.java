package lostark.todo.domainV2.character.repository;

import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.character.entity.Character;

import java.util.List;
import java.util.Optional;

public interface CharacterCustomRepository  {

    Optional<Character> getByIdAndUsername(long characterId, String username);

    List<Character> getCharacterList(String username);

    long deleteByMember(Member member);

    List<DashboardResponse> searchCharactersDashBoard(int limit);

    long updateDayContentGauge();

    long saveBeforeGauge();

    long updateDayContentCheck();

    void updateDayContentPriceChaos(DayContent dayContent, Double price);

    void updateDayContentPriceGuardian(DayContent dayContent, Double price);
}