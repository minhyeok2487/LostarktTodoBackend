package lostark.todo.service.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v1.dto.marketDto.AuctionRequestDtoV1;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MarketServiceV2 {

    private final MarketRepository marketRepository;

    /**
     * 거래소 데이터 저장 메소드
     * 기존 데이터가 있으면 가격 교체
     * 없으면 그냥 저장
     */
    public String saveMarketItemList(List<Market> marketList, int categoryCode) {
        List<Market> oldList = marketRepository.findByCategoryCode(categoryCode);

        if (oldList.isEmpty()) {
            List<Market> markets = marketRepository.saveAll(marketList);
            return "총 " + markets.size() +"개 저장 완료";
        } else {
            oldList.forEach(old -> {
                List<Market> matchingNews = marketList.stream()
                        .filter(news -> old.getName().equals(news.getName()))
                        .collect(Collectors.toList());
                if (!matchingNews.isEmpty()) {
                    old.changeData(matchingNews.get(0));
                }
            });
            return "총 " + oldList.size() +"개 업데이트 완료";
        }
    }

    /**
     * 경매장 데이터 저장 메소드
     * 기존 데이터가 있으면 가격 교체
     * 없으면 그냥 저장
     */
    public String  saveAuctionItem(JSONObject auctionItem, AuctionRequestDtoV1 auctionRequestDtoV1) {
        String itemName = auctionRequestDtoV1.getItemName();
        int categoryCode = auctionRequestDtoV1.getCategoryCode();

        Market oldItem = marketRepository.findByName(itemName).orElse(null);
        Market market = Market.createAuctionItem(auctionItem, itemName, categoryCode);

        Market result = (oldItem == null) ? marketRepository.save(market) : oldItem.changeData(market);

        return itemName + "저장 및 업데이트 완료";
    }


    /**
     * 거래소 데이터 호출
     */
    public Map<String, MarketContentResourceDto> getContentResource(List<String> names) {
        List<Market> marketByNames = marketRepository.findByNameIn(names);
        Map<String , MarketContentResourceDto> contentResourceDtoHashMap = new HashMap<>();
        for (Market marketByName : marketByNames) {
            MarketContentResourceDto dto = MarketContentResourceDto.builder()
                    .recentPrice(marketByName.getRecentPrice())
                    .bundleCount(marketByName.getBundleCount())
                    .build();
            contentResourceDtoHashMap.put(marketByName.getName(), dto);
        }
        return contentResourceDtoHashMap;
    }

    public List<String> dayContentResource() {
        List<String> dayContentResource = new ArrayList<>();
        dayContentResource.add("정제된 파괴강석");
        dayContentResource.add("정제된 수호강석");
        dayContentResource.add("찬란한 명예의 돌파석");

        dayContentResource.add("파괴강석");
        dayContentResource.add("수호강석");
        dayContentResource.add("경이로운 명예의 돌파석");

        dayContentResource.add("파괴석 결정");
        dayContentResource.add("수호석 결정");
        dayContentResource.add("위대한 명예의 돌파석");
        dayContentResource.add("1레벨");
        return dayContentResource;
    }



}
