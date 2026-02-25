package lostark.todo.domain.character.entity;

import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.character.enums.goldCheckPolicy.GoldCheckPolicyEnum;
import lostark.todo.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@DisplayName("캐릭터 생성 시 WeekTodo/Settings 초기화 테스트")
class CharacterCreateTest {

    private Character createCharacterWithDefaults() {
        Member member = Member.builder()
                .id(1L)
                .username("test@test.com")
                .mainCharacter("테스트캐릭터")
                .build();

        DayTodo dayTodo = DayTodo.builder()
                .chaosCheck(0)
                .chaosGauge(0)
                .chaosGold(0)
                .guardianCheck(0)
                .guardianGauge(0)
                .guardianGold(0)
                .eponaCheck2(0)
                .eponaGauge(0)
                .beforeChaosGauge(0)
                .beforeGuardianGauge(0)
                .beforeEponaGauge(0)
                .weekTotalGold(0)
                .build();

        return Character.builder()
                .id(1L)
                .characterName("테스트캐릭터")
                .characterLevel(70)
                .characterClassName("버서커")
                .serverName("루페온")
                .itemLevel(1620.0)
                .combatPower(50000.0)
                .sortNumber(0)
                .goldCharacter(false)
                .member(member)
                .dayTodo(dayTodo)
                .weekTodo(new WeekTodo())
                .settings(new Settings())
                .todoV2List(new ArrayList<>())
                .raidBusGoldList(new ArrayList<>())
                .isDeleted(false)
                .build();
    }

    @Nested
    @DisplayName("WeekTodo 기본 생성자 테스트")
    class WeekTodoDefaultsTest {

        @Test
        @DisplayName("new WeekTodo() 시 halHourglass는 false")
        void halHourglass_defaultFalse() {
            WeekTodo weekTodo = new WeekTodo();

            assertThat(weekTodo.isHalHourglass()).isFalse();
            assertThat(weekTodo.getWeekEpona()).isEqualTo(0);
            assertThat(weekTodo.isSilmaelChange()).isFalse();
            assertThat(weekTodo.getCubeTicket()).isEqualTo(0);
            assertThat(weekTodo.getElysianCount()).isEqualTo(0);
            assertThat(weekTodo.getHellKey()).isEqualTo(0);
        }

        @Test
        @DisplayName("WeekTodo.builder()로 생성 시 halHourglass 미지정하면 false")
        void builder_halHourglass_defaultFalse() {
            WeekTodo weekTodo = WeekTodo.builder()
                    .weekEpona(0)
                    .silmaelChange(false)
                    .cubeTicket(0)
                    .elysianCount(0)
                    .hellKey(0)
                    .build();

            assertThat(weekTodo.isHalHourglass()).isFalse();
        }
    }

    @Nested
    @DisplayName("Settings 기본 생성자 테스트")
    class SettingsDefaultsTest {

        @Test
        @DisplayName("new Settings() 시 showHalHourglass는 true")
        void showHalHourglass_defaultTrue() {
            Settings settings = new Settings();

            assertThat(settings.isShowHalHourglass()).isTrue();
            assertThat(settings.isShowHellKey()).isTrue();
            assertThat(settings.isShowElysian()).isTrue();
            assertThat(settings.isShowCubeTicket()).isTrue();
            assertThat(settings.isShowSilmaelChange()).isTrue();
            assertThat(settings.isShowCharacter()).isTrue();
            assertThat(settings.getGoldCheckPolicyEnum()).isEqualTo(GoldCheckPolicyEnum.TOP_THREE_POLICY);
        }

        @Test
        @DisplayName("Settings.update()로 showHalHourglass를 false로 변경")
        void update_showHalHourglass() {
            Settings settings = new Settings();

            settings.update("showHalHourglass", false);

            assertThat(settings.isShowHalHourglass()).isFalse();
        }

        @Test
        @DisplayName("Settings.update()에 존재하지 않는 showTrialSand를 넣으면 예외")
        void update_showTrialSand_throws() {
            Settings settings = new Settings();

            assertThatThrownBy(() -> settings.update("showTrialSand", true))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("캐릭터 생성 후 CharacterResponse 변환 테스트")
    class CharacterResponseTest {

        @Test
        @DisplayName("새 캐릭터의 CharacterResponse에 halHourglass=false 포함")
        void toDto_halHourglass() {
            Character character = createCharacterWithDefaults();

            CharacterResponse response = new CharacterResponse().toDto(character);

            assertThat(response.isHalHourglass()).isFalse();
            assertThat(response.getSettings().isShowHalHourglass()).isTrue();
        }

        @Test
        @DisplayName("halHourglass 토글 후 CharacterResponse 반영 확인")
        void toDto_halHourglass_afterToggle() {
            Character character = createCharacterWithDefaults();
            character.getWeekTodo().updateHalHourglass();

            CharacterResponse response = new CharacterResponse().toDto(character);

            assertThat(response.isHalHourglass()).isTrue();
        }

        @Test
        @DisplayName("showHalHourglass=false 시 settings 반영 확인")
        void toDto_showHalHourglass_false() {
            Character character = createCharacterWithDefaults();
            character.getSettings().update("showHalHourglass", false);

            CharacterResponse response = new CharacterResponse().toDto(character);

            assertThat(response.getSettings().isShowHalHourglass()).isFalse();
        }
    }
}
