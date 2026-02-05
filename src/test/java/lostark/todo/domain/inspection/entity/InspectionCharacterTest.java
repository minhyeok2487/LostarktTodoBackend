package lostark.todo.domain.inspection.entity;

import lostark.todo.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class InspectionCharacterTest {

    private InspectionCharacter character;

    @BeforeEach
    void setUp() {
        character = InspectionCharacter.builder()
                .id(1L)
                .member(Member.builder().id(1L).build())
                .characterName("테스트캐릭터")
                .serverName("루페온")
                .characterClassName("버서커")
                .characterImage("https://img.test.com/old.png")
                .itemLevel(1600.0)
                .combatPower(2000.0)
                .noChangeThreshold(3)
                .isActive(true)
                .histories(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("updateProfile 메서드")
    class UpdateProfileTest {

        @Test
        @DisplayName("성공 - 프로필 정보 업데이트")
        void success() {
            // when
            character.updateProfile(
                    "https://img.test.com/new.png",
                    1620.0,
                    2200.0,
                    "카제로스",
                    "워로드"
            );

            // then
            assertThat(character.getCharacterImage()).isEqualTo("https://img.test.com/new.png");
            assertThat(character.getItemLevel()).isEqualTo(1620.0);
            assertThat(character.getCombatPower()).isEqualTo(2200.0);
            assertThat(character.getServerName()).isEqualTo("카제로스");
            assertThat(character.getCharacterClassName()).isEqualTo("워로드");
        }
    }

    @Nested
    @DisplayName("updateSettings 메서드")
    class UpdateSettingsTest {

        @Test
        @DisplayName("성공 - 설정 변경")
        void success() {
            // when
            character.updateSettings(7, false);

            // then
            assertThat(character.getNoChangeThreshold()).isEqualTo(7);
            assertThat(character.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("기본값 테스트")
    class DefaultValueTest {

        @Test
        @DisplayName("noChangeThreshold 기본값은 3")
        void defaultThreshold() {
            InspectionCharacter newChar = InspectionCharacter.builder()
                    .characterName("새캐릭터")
                    .build();

            assertThat(newChar.getNoChangeThreshold()).isEqualTo(3);
        }

        @Test
        @DisplayName("isActive 기본값은 true")
        void defaultIsActive() {
            InspectionCharacter newChar = InspectionCharacter.builder()
                    .characterName("새캐릭터")
                    .build();

            assertThat(newChar.isActive()).isTrue();
        }
    }
}
