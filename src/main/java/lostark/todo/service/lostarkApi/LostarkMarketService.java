package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
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
    private final MemberService memberService;
    private final MarketRepository marketRepository;


    public List<Market> getMarketData(int categoryCode, Long memberId) throws Exception {
        Member member = memberService.findUser(memberId);

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
}
