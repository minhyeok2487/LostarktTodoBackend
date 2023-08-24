package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.marketDto.MarketDto;
import lostark.todo.controller.dto.marketDto.MarketListDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.market.CategoryCode;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/lostark/market")
@Api(tags = {"로스트아크 연동 거래소, 경매장 API"})
public class LostarkMarketApiController {

    private final MemberService memberService;
    private final LostarkMarketService lostarkMarketService;
    private final MarketService marketService;

    @ApiOperation(value = "로스트아크 api로부터 해당 카테고리 코드의 거래소 데이터를 불러와서 DB에 저장",
            notes = "기존 데이터가 있으면 갱신, 관리자만 사용가능", response = MarketListDto.class)
    @GetMapping("/{categoryCode}")
    public ResponseEntity getMarketCategoryCode(@AuthenticationPrincipal String username, @PathVariable CategoryCode categoryCode)  {
        Member member = memberService.findMember(username);
        if (member.getRole().equals(Role.ADMIN)) {
            // 데이터 불러오기
            List<Market> marketList = lostarkMarketService.getMarketData(categoryCode.getValue(), member.getApiKey());
            List<Market> markets;
            if (marketService.checkMarketItemList(categoryCode.getValue())) {
                markets = marketService.updateMarketItemList(marketList, categoryCode.getValue());
            } else {
                markets = marketService.createMarketItemList(marketList);
            }

            MarketListDto marketListDto = MarketListDto.builder()
                    .marketDtoList(markets.stream().map(
                            market -> MarketDto.builder()
                                    .name(market.getName())
                                    .bundleCount(market.getBundleCount())
                                    .recentPrice(market.getRecentPrice())
                                    .build()).collect(Collectors.toList()))
                    .build();
            return new ResponseEntity(marketListDto, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
    }

//    @ApiOperation(value = "로스트아크 api로부터 해당 정보의 경매장 데이터를 불러와서 DB에 저장",
//            notes = "기존 데이터가 있으면 갱신", response = String.class)
//    @PostMapping("/auction")
//    public ResponseEntity getAuction(@RequestBody AuctionRequestDto auctionRequestDto) {
//        Member member = memberService.findMember(auctionRequestDto.getUsername());
//
//        // 데이터 불러오기
//        JSONObject auctionItem = lostarkMarketService.getAuctionItems(auctionRequestDto, member.getApiKey());
//
//        return new ResponseEntity(marketService.saveAuctionItem(auctionItem, auctionRequestDto), HttpStatus.OK);
//    }

}
