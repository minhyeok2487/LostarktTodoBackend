package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.marketDto.AuctionRequestDto;
import lostark.todo.domain.market.Market;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkMarketService {

    private final LostarkApiService lostarkApiService;

    public List<Market> getMarketData(int categoryCode, String apiKey) {

        int pageNo = 0;
        JSONObject jsonObject = getMarketDataPaging(categoryCode, apiKey, pageNo);
        List<Market> result = new ArrayList<>();
        // 모든 데이터 불러오기 추가
        int pageSize = 10;
        int totalCount = Integer.parseInt(jsonObject.get("TotalCount").toString());
        while (pageNo*pageSize < totalCount) {
            pageNo++;
            JSONObject data = getMarketDataPaging(categoryCode, apiKey, pageNo);
            JSONArray resultJsonArray = (JSONArray) data.get("Items");
            for(int i = 0; i < resultJsonArray.size(); i++) {
                JSONObject tempJson = (JSONObject) resultJsonArray.get(i);
                Market market = new Market(tempJson, categoryCode);
                result.add(market);
            }
        }
        return result;
    }

    private JSONObject getMarketDataPaging(int categoryCode, String apiKey, int pageNo) {
        try {
            String link = "https://developer-lostark.game.onstove.com/markets/items";
            String parameter = "{\n"
                    + "  \"Sort\": \"RECENT_PRICE\",\n"
                    + "  \"CategoryCode\": "+ categoryCode +",\n"
                    + "  \"PageNo\": "+ pageNo +",\n"
                    + "  \"SortCondition\": \"DESC\"\n"
                    + "}";
            InputStreamReader inputStreamReader = lostarkApiService.lostarkPostApi(link, parameter, apiKey);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(inputStreamReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 경매장 데이터 가져오기
     * 3티어, 가장 값이 낮은 아이템 가져옴
     */
    public JSONObject getAuctionItems(AuctionRequestDto auctionRequestDto, String apiKey) {
        int categoryCode = auctionRequestDto.getCategoryCode();
        String itemName = auctionRequestDto.getItemName();
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
            InputStreamReader inputStreamReader = lostarkApiService.lostarkPostApi(link, parameter, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(inputStreamReader);

            JSONArray jsonArray = (JSONArray) jsonObject.get("Items");
            JSONObject item = (JSONObject) jsonArray.get(0);
            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
