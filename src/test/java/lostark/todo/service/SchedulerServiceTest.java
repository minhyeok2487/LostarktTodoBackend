package lostark.todo.service;

import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SchedulerServiceTest {

    @Autowired CharacterService characterService;
    @Autowired LostarkMarketService lostarkMarketService;
    @Autowired MarketService marketService;
    @Autowired ContentService contentService;
    @Autowired MemberService memberService;
    
    @Test
    @DisplayName("매일 오전 6시 일일 숙제 초기화 성공")
    void resetDayTodoTest() {
        // given
        Map<String, Market> contentResource = marketService.getContentResource();
        Map<String, DayContent> dayContent = contentService.findDayContent();

        Member member = memberService.findMember("test01");

        // when
        member.getCharacters().forEach(character -> {
            // 원활한 테스트를 위해 휴식게이지 0, 숙제를 안했을 때 가정
            character.getDayTodo().setChaosCheck(0);
            character.getDayTodo().setChaosGauge(0);
            character.getDayTodo().setGuardianCheck(0);
            character.getDayTodo().setGuardianGauge(0);

            character.getDayTodo().setEponaCheck(false);
            character.getDayTodo().calculateChaos();
            character.getDayTodo().calculateGuardian();
            characterService.calculateDayTodo(character, contentResource, dayContent);
        });

        // then
        Member updatedMember = memberService.findMember("test01");
        updatedMember.getCharacters().forEach(character -> {
            assertThat(character.getDayTodo().isEponaCheck()).isEqualTo(false);
            assertThat(character.getDayTodo().getChaosCheck()).isEqualTo(0);
            assertThat(character.getDayTodo().getGuardianCheck()).isEqualTo(0);
            assertThat(character.getDayTodo().getChaosGauge()).isEqualTo(20);
            assertThat(character.getDayTodo().getGuardianGauge()).isEqualTo(10);
        });

    }
}