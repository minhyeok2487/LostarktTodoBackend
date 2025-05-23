package lostark.todo.domain.lostark.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.schedule.dto.AuctionRequestDto;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
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
public class LostarkMarketApiClient {

    private final LostarkApiClient lostarkApiClient;

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
                Market market = Market.builder()
                        .lostarkMarketId(Long.parseLong(tempJson.get("Id").toString()))
                        .yDayAvgPrice(Double.parseDouble(tempJson.get("YDayAvgPrice").toString()))
                        .currentMinPrice(Integer.parseInt(tempJson.get("CurrentMinPrice").toString()))
                        .grade(tempJson.get("Grade").toString())
                        .recentPrice(Integer.parseInt(tempJson.get("RecentPrice").toString()))
                        .icon(tempJson.get("Icon").toString())
                        .name(tempJson.get("Name").toString())
                        .bundleCount(Integer.parseInt(tempJson.get("BundleCount").toString()))
                        .categoryCode(categoryCode)
                        .build();
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
            InputStreamReader inputStreamReader = lostarkApiClient.lostarkPostApi(link, parameter, apiKey);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(inputStreamReader);
        } catch (Exception e) {
            throw new ConditionNotMetException("올바르지 않은 categoryCode");
        }
    }


    /**
     * 경매장 데이터 가져오기
     * 가장 값이 낮은 아이템 가져옴
     */
    public JSONObject getAuctionItems(AuctionRequestDto request, String apiKey) {
        try {
            String link = "https://developer-lostark.game.onstove.com/auctions/items";
            String parameter = "{"
                    + "Sort : \"BUY_PRICE\""
                    + ",CategoryCode : " + request.getCategoryCode()
                    + ",ItemTier : " + request.getItemTier()
                    + ",ItemName : \""+ request.getItemName() +"\""
                    + ",PageNo : 1"
                    + ",SortCondition : \"ASC\""
                    + "}";
            InputStreamReader inputStreamReader = lostarkApiClient.lostarkPostApi(link, parameter, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(inputStreamReader);

            JSONArray jsonArray = (JSONArray) jsonObject.get("Items");
            return (JSONObject) jsonArray.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
