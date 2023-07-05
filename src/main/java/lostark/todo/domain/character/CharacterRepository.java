package lostark.todo.domain.character;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    List<Character> findByMember_Id(Long member_id);

    List<Character> findByMember_IdAndSelectedOrderByItemLevelDesc(Long member_id, boolean select);

    Character findByCharacterName(String characterName);
}
