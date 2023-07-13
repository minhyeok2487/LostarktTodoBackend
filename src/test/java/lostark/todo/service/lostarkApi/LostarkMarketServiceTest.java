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
        String username = "qwe2487";
        Member member = memberRepository.findByUsername("qwe2487")
                .orElseThrow(() -> new IllegalArgumentException(username+"은(는) 없는 회원 입니다."));
        MarketReturnDto auctionItems = lostarkMarketService.getAuctionItems(auctionDto, member);
        System.out.println("auctionItems = " + auctionItems);
    }
}