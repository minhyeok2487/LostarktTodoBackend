package lostark.todo.service.marketService;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.market.CategoryCode;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MarketServiceTest {

    @Autowired MemberService memberService;
    @Autowired LostarkMarketService lostarkMarketService;
    @Autowired MarketService marketService;
    
    String username;

    Member member;
    @BeforeEach
    void init() {
        username = "test";
        member = memberService.findMember(username);
    }

    @Test
    @DisplayName("createMarketItemList 테스트 성공")
    void createMarketItemListTest() {
        // given
        CategoryCode categoryCode = CategoryCode.재련재료;

        // when
        List<Market> marketList = lostarkMarketService.getMarketData(categoryCode.getValue(), member.getApiKey());
        List<Market> marketItemList = marketService.createMarketItemList(marketList);

        // then
        assertThat(marketList).isEqualTo(marketItemList);
    }

    @Test
    @DisplayName("createMarketItemList 테스트 실패 : 올바르지 않은 categoryCode")
    void createMarketCategoryCodeError() {

        // when
        int categoryCode = 1;

        // then
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            lostarkMarketService.getMarketData(categoryCode, member.getApiKey());
        });
        assertThat(exception.getMessage()).isEqualTo("올바르지 않은 categoryCode");
    }

    @Test
    @DisplayName("MarketList is Null")
    void createMarketListNull() {

        // given
        List<Market> marketList = null;

        // when
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            marketService.createMarketItemList(marketList);
        });
        assertThat(exception.getMessage()).isEqualTo(null);
    }

    @Test
    @DisplayName("MarketList is Empty")
    void createMarketListEmpty() {

        // given
        List<Market> marketList = new ArrayList<>();

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            marketService.createMarketItemList(marketList);
        });
        assertThat(exception.getMessage()).isEqualTo("marketList is Empty");
    }
}
