package lostark.todo.controller.apiLostark;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.marketDto.AuctionRequestDto;
import lostark.todo.controller.dto.marketDto.MarketReturnDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

    private final MemberService memberService;
    private final LostarkMarketService lostarkMarketService;
    private final MarketService marketService;

    @PostMapping("/{categoryCode}")
    public ResponseEntity getMarketCategoryCode(@RequestBody MemberRequestDto requestDto, @PathVariable int categoryCode)  {
        Member member = memberService.findMember(requestDto.getUsername());
        List<Market> marketList = lostarkMarketService.getMarketData(categoryCode, member.getApiKey());
        List<Market> markets = marketService.saveMarketItemList(marketList, categoryCode);
        return new ResponseEntity(markets, HttpStatus.OK);
    }

    @PostMapping("/auction")
    public ResponseEntity getAuction(@RequestBody AuctionRequestDto auctionRequestDto) {
        Member member = memberService.findMember(auctionRequestDto.getUsername());
        JSONObject auctionItem = lostarkMarketService.getAuctionItems(auctionRequestDto, member.getApiKey());
        MarketReturnDto marketReturnDto = marketService.saveAuctionItem(auctionItem, auctionRequestDto);
        return new ResponseEntity(marketReturnDto, HttpStatus.OK);
    }

}
