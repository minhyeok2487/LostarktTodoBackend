package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.marketDto.AuctionDto;
import lostark.todo.controller.dto.marketDto.MarketReturnDto;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkMarketService {

    private final LostarkApiService lostarkApiService;
    private final MarketRepository marketRepository;

    // 거래소 데이터 가져와서 marketdb에 저장
    public List<Market> getMarketData(int categoryCode, Member member) throws Exception {

        int pageNo = 0;
        JSONObject jsonObject = getMarketDataPaging(categoryCode, member, pageNo);

        // 모든 데이터 불러오기 추가
        int pageSize = 10;
        int totalCount = Integer.parseInt(jsonObject.get("TotalCount").toString());
        while (pageNo*pageSize < totalCount) {
            pageNo++;
            JSONObject data = getMarketDataPaging(categoryCode, member, pageNo);
            JSONArray resultJsonArray = (JSONArray) data.get("Items");
            for(int i = 0; i < resultJsonArray.size(); i++) {
                JSONObject tempJson = (JSONObject) resultJsonArray.get(i);
                Market market = new Market(tempJson, categoryCode);
                marketRepository.save(market);
            }
        }
        return marketRepository.findByCategoryCodeOrderByCurrentMinPriceDesc(categoryCode);
    }

    private JSONObject getMarketDataPaging(int categoryCode, Member member, int pageNo) throws IOException, ParseException {
        String link = "https://developer-lostark.game.onstove.com/markets/items";
        String parameter = "{\n"
                + "  \"Sort\": \"RECENT_PRICE\",\n"
                + "  \"CategoryCode\": "+ categoryCode +",\n"
                + "  \"PageNo\": "+ pageNo +",\n"
                + "  \"SortCondition\": \"DESC\"\n"
                + "}";
        InputStreamReader inputStreamReader = lostarkApiService.LostarkPostApi(link, parameter, member.getApiKey());
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(inputStreamReader);
        return jsonObject;
    }

    //경매장 데이터 가져오기
    public MarketReturnDto getAuctionItems(AuctionDto auctionDto, Member member) {
        int categoryCode = auctionDto.getCategoryCode();
        String itemName = auctionDto.getItemName();
        try {
            String link = "https://developer-lostark.game.onstove.com/auctions/items";
            String parameter = "{"
                    + "Sort : \"BUY_PRICE\""
                    + ",CategoryCode : " + categoryCode
                    + ",ItemTier : 3"
                    + ",ItemName : \""+ itemName +"\""
                    + ",PageNo : 1"
                    + ",SortCondition : \"ASC\""
                    + "}";
            InputStreamReader inputStreamReader = lostarkApiService.LostarkPostApi(link, parameter, member.getApiKey());
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(inputStreamReader);

            JSONArray jsonArray = (JSONArray) jsonObject.get("Items");
            JSONObject item = (JSONObject) jsonArray.get(0);

            Market market = Market.createAuctionItem(item, itemName, categoryCode);
            Market saved = marketRepository.save(market);
            MarketReturnDto marketReturnDto = new MarketReturnDto(saved);
            return marketReturnDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
