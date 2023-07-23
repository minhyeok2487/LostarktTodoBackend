package lostark.todo.service;

import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MarketServiceTest {

    @Autowired
    MarketService marketService;

    @Autowired
    LostarkMarketService lostarkMarketService;

    @Autowired
    MemberService memberService;

    @Test
    void saveMarketList() {
        int categoryCode = 50000;
        String username = "qwe2487";
        Member member = memberService.findMember(username);
        List<Market> marketList = lostarkMarketService.getMarketData(categoryCode, member.getApiKey());
        List<Market> markets = marketService.saveMarketItemList(marketList, categoryCode);
        for (Market market : markets) {
            System.out.print("market.getName() = " + market.getName() +" / ");
            System.out.println("market.getCurrentMinPrice() = " + market.getCurrentMinPrice());
        }
    }
}