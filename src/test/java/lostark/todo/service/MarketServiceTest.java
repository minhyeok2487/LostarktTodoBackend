package lostark.todo.service;

import lostark.todo.domain.market.Market;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MarketServiceTest {

    @Autowired MarketService marketService;

    private static List<String> makeDayContentResourceNames() {
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
        return dayContentResource;
    }

    @Test
    public void getMarketByNamesTest() {
        List<String> dayContentResource = makeDayContentResourceNames();
        List<Market> marketByNames = marketService.getMarketByNames(dayContentResource);
        for (Market marketByName : marketByNames) {
            System.out.println("marketByName = " + marketByName.getName());
        }
    }
}