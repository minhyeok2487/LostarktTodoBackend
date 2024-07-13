package lostark.todo.domain.character;

import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface CharacterCustomRepository  {

    Optional<Character> getByIdAndUsername(long characterId, String username);

    long deleteByMember(Member member);

    List<DashboardResponse> searchCharactersDashBoard(int limit);

    long updateDayContentGauge();

    long saveBeforeGauge();

    long updateDayContentCheck();

    void updateDayContentPriceChaos(DayContent dayContent, Double price);

    void updateDayContentPriceGuardian(DayContent dayContent, Double price);
}