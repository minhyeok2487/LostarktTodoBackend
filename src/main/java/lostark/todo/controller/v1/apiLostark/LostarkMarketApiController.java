package lostark.todo.controller.v1.apiLostark;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v1.dto.marketDto.AuctionRequestDto;
import lostark.todo.controller.v1.dto.memberDto.MemberRequestDto;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.v1.MarketService;
import lostark.todo.service.v1.MemberService;
import lostark.todo.service.v1.lostarkApi.LostarkMarketService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/lostark/market")
@Api(tags = {"로스트아크 연동 거래소, 경매장 REST API"})
public class LostarkMarketApiController {

    private final MemberService memberService;
    private final LostarkMarketService lostarkMarketService;
    private final MarketService marketService;

    @ApiOperation(value = "로스트아크 api로부터 해당 카테고리 코드의 거래소 데이터를 불러와서 DB에 저장",
            notes = "기존 데이터가 있으면 갱신", response = String.class)
    @PostMapping("/{categoryCode}")
    public ResponseEntity getMarketCategoryCode(@RequestBody MemberRequestDto requestDto, @PathVariable int categoryCode)  {
        Member member = memberService.findMember(requestDto.getUsername());

        // 데이터 불러오기
        List<Market> marketList = lostarkMarketService.getMarketData(categoryCode, member.getApiKey());

        // 저장
        return new ResponseEntity(marketService.saveMarketItemList(marketList, categoryCode), HttpStatus.OK);
    }

    @ApiOperation(value = "로스트아크 api로부터 해당 정보의 경매장 데이터를 불러와서 DB에 저장",
            notes = "기존 데이터가 있으면 갱신", response = String.class)
    @PostMapping("/auction")
    public ResponseEntity getAuction(@RequestBody AuctionRequestDto auctionRequestDto) {
        Member member = memberService.findMember(auctionRequestDto.getUsername());

        // 데이터 불러오기
        JSONObject auctionItem = lostarkMarketService.getAuctionItems(auctionRequestDto, member.getApiKey());

        return new ResponseEntity(marketService.saveAuctionItem(auctionItem, auctionRequestDto), HttpStatus.OK);
    }

}
