package lostark.todo.service.lostarkApi;

import lostark.todo.controller.dto.marketDto.AuctionDto;
import lostark.todo.controller.dto.marketDto.MarketReturnDto;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import lostark.todo.service.MemberService;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LostarkMarketServiceTest {

    @Autowired LostarkMarketService lostarkMarketService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void getAuctionItemsTest() {
        AuctionDto auctionDto = new AuctionDto("1레벨", 210000);
        Member member = memberRepository.findByUsername("qwe2487");
        MarketReturnDto auctionItems = lostarkMarketService.getAuctionItems(auctionDto, member);
        System.out.println("auctionItems = " + auctionItems);
    }
}