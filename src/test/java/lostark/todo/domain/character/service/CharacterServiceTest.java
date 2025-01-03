package lostark.todo.domain.character.service;

import lostark.todo.data.MemberTestData;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @InjectMocks
    CharacterService characterService;

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
}