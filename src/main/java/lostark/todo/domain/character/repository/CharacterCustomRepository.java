package lostark.todo.domain.character.repository;

import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.domain.character.dto.DeletedCharacterResponse;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.entity.Character;

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

    List<Character> getCharacter(String characterName);

    List<DeletedCharacterResponse> getDeletedCharacter(String username);
}