package lostark.todo.domain.character.service;

import lostark.todo.data.MemberTestData;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @InjectMocks
    CharacterService characterService;

    @Mock
    CharacterRepository characterRepository;

    @DisplayName("캐릭터 일일컨텐츠 체크 전체 업데이트 성공")
    @Test
    void updateDayCheckAll_Success() {
        //given
        Member mockMember = MemberTestData.createMockMember();
        Character mockCharacter = mockMember.getCharacters().get(0);

        //when
        characterService.updateDayCheckAll(mockCharacter);

        //then
        Assertions.assertThat(mockCharacter.getDayTodo().getEponaCheck2()).isEqualTo(3);
        Assertions.assertThat(mockCharacter.getDayTodo().getChaosCheck()).isEqualTo(2);
        Assertions.assertThat(mockCharacter.getDayTodo().getGuardianCheck()).isEqualTo(1);
    }

    @DisplayName("캐릭터 일일컨텐츠 체크 전체 업데이트 성공(에포나 미출력)")
    @Test
    void updateDayCheckAll_Success_WithoutEponaCheck() {
        //given
        Member mockMember = MemberTestData.createMockMember();
        Character mockCharacter = mockMember.getCharacters().get(0);

        //when
        mockCharacter.getSettings().setShowEpona(false);
        characterService.updateDayCheckAll(mockCharacter);

        //then
        Assertions.assertThat(mockCharacter.getDayTodo().getEponaCheck2()).isEqualTo(0);
        Assertions.assertThat(mockCharacter.getDayTodo().getChaosCheck()).isEqualTo(2);
        Assertions.assertThat(mockCharacter.getDayTodo().getGuardianCheck()).isEqualTo(1);
    }

    @DisplayName("골드 획득 캐릭터 지정 성공")
    @Test
    void updateGoldCharacter_Success() {
        //given
        Member mockMember = MemberTestData.createMockMember();
        Character mockCharacter = mockMember.getCharacters().stream()
                .filter(character -> !character.isGoldCharacter()).findFirst().orElseThrow();

        //when
        when(characterRepository.countByMemberAndServerNameAndGoldCharacterIsTrue(mockMember, mockCharacter.getServerName())).thenReturn(5);
        characterService.updateGoldCharacter(mockCharacter);

        //then
        Assertions.assertThat(mockCharacter.isGoldCharacter()).isTrue();
    }
}