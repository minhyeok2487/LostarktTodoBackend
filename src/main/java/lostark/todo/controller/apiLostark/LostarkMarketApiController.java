package lostark.todo.controller.apiLostark;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.marketDto.AuctionDto;
import lostark.todo.controller.dto.marketDto.MarketReturnDto;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/lostark/market")
public class LostarkMarketApiController {

    private final LostarkMarketService marketService;
    private final MemberService memberService;

    @GetMapping("/{categoryCode}")
    public ResponseEntity getMarketCategoryCode(@PathVariable int categoryCode, HttpServletRequest request) throws Exception {
        String username = request.getHeader("username");
        Member member = memberService.findMember(username);
        List<Market> marketList = marketService.getMarketData(categoryCode, member);
        return new ResponseEntity(marketList, HttpStatus.OK);
    }

    @PostMapping("/auction")
    public ResponseEntity getAuction(HttpServletRequest request, @RequestBody AuctionDto auctionDto) {
        String username = request.getHeader("username");
        Member member = memberService.findMember(username);
        System.out.println("auctionDto = " + auctionDto);
        MarketReturnDto auctionItems = marketService.getAuctionItems(auctionDto, member);
        return new ResponseEntity(auctionItems, HttpStatus.OK);
    }

}
