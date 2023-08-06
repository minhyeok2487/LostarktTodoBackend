package lostark.todo.controller.controller;

import lombok.*;
import lostark.todo.controller.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.service.MarketService;
import lostark.todo.service.v2.ContentServiceV2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final ContentServiceV2 contentServiceV2;
    private final MarketService marketService;


    @GetMapping("/")
    public String test(Model model) {
        //일일컨텐츠 수익 추출
        List<ContentPriceDto> contentPriceDtoList = getContentPriceDtos();

        model.addAttribute("contentPriceDtoList", contentPriceDtoList);
        return "index";
    }


    /**
     * 일일컨텐츠 수익 추출
     */
    private List<ContentPriceDto> getContentPriceDtos() {
        // 컨텐츠 데이터 호출
        List<DayContent> contentList = contentServiceV2.findAllDayContent();

        // 재련재료 데이터 리스트 호출
        List<String> resource = marketService.dayContentResource();

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(resource);

        List<ContentPriceDto> contentPriceDtoList = new ArrayList<>();

        // 캐릭터 리스트와 거래소 데이터를 이용한 계산
        MarketContentResourceDto destruction = null;
        MarketContentResourceDto guardian = null;
        MarketContentResourceDto leapStone = null;
        for (DayContent content : contentList) {
            if (content.getLevel() >= 1415) {
                destruction = contentResource.get("파괴석 결정");
                guardian = contentResource.get("수호석 결정");
                leapStone = contentResource.get("위대한 명예의 돌파석");
            }
            if (content.getLevel() >= 1540) {
                destruction = contentResource.get("파괴강석");
                guardian = contentResource.get("수호강석");
                leapStone = contentResource.get("경이로운 명예의 돌파석");
            }
            if (content.getLevel() >= 1580) {
                destruction = contentResource.get("정제된 파괴강석");
                guardian = contentResource.get("정제된 수호강석");
                leapStone = contentResource.get("찬란한 명예의 돌파석");
            }
            MarketContentResourceDto jewelry = contentResource.get("1레벨");
            if(content.getContentCategory().equals(Category.카오스던전)) {
                ContentPriceDto contentPriceDto = ContentPriceDto.builder()
                        .price(calculateChaos(content, destruction, guardian, jewelry))
                        .category(Category.카오스던전.toString())
                        .contentName(content.getName())
                        .level(content.getLevel())
                        .count(2)
                        .build();
                contentPriceDtoList.add(contentPriceDto);
            }
            if(content.getContentCategory().equals(Category.가디언토벌)) {
                ContentPriceDto contentPriceDto = ContentPriceDto.builder()
                        .price(calculateGuardian(content, destruction, guardian, leapStone))
                        .category(Category.가디언토벌.toString())
                        .contentName(content.getName())
                        .level(content.getLevel())
                        .count(1)
                        .build();
                contentPriceDtoList.add(contentPriceDto);
            }
        }
        Collections.sort(contentPriceDtoList, new PriceComparator());
        return contentPriceDtoList;
    }


    public double calculateChaos(DayContent content,
                               MarketContentResourceDto destruction,
                               MarketContentResourceDto guardian,
                               MarketContentResourceDto jewelry) {
        double price = 0;
        price = calculateBundle(destruction, content.getDestructionStone(), price) * 2;
        price = calculateBundle(guardian, content.getGuardianStone(), price) * 2;
        price = calculateBundle(jewelry, content.getJewelry(), price) * 2;

        return price;
    }


    private double calculateGuardian(DayContent content,
                                     MarketContentResourceDto destruction,
                                     MarketContentResourceDto guardian,
                                     MarketContentResourceDto leapStone) {
        double price = 0;
        price = calculateBundle(destruction, content.getDestructionStone(), price) * 2;
        price = calculateBundle(guardian, content.getGuardianStone(), price) * 2;
        price = calculateBundle(leapStone, content.getLeapStone(), price) * 2;
        return price;
    }


    /**
     * 번들(묶음) 계산
     */
    private double calculateBundle(MarketContentResourceDto dto, double count, double price) {
        price += (dto.getRecentPrice() * count) / dto.getBundleCount();
        return Math.round(price * 100.0) / 100.0;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ContentPriceDto {

        private String category;

        private String contentName;

        private int count;

        private double level;

        private double price;
    }

    class PriceComparator implements Comparator<ContentPriceDto> {
        @Override
        public int compare(ContentPriceDto c1, ContentPriceDto c2) {
            if (c1.price > c2.price) {
                return -1;
            } else if (c1.price < c2.price) {
                return 1;
            }
            return 0;
        }
    }
}
