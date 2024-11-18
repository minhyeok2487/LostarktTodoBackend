package lostark.todo.data;

import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.Content;
import lostark.todo.domain.content.DayContent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentTestData {

    // 카테고리별로 그룹화된 맵 반환
    public static Map<Category, List<DayContent>> createMockDayContentMap() {
        return createMockDayContents().stream()
                .collect(Collectors.groupingBy(Content::getCategory));
    }

    public static List<DayContent> createMockDayContents() {
        return Arrays.asList(
                // 카오스던전
                createDayContent(1L, Category.카오스던전, "타락1", 1415, 72415, 2438, 4.9, 76.4, 226.4, 7),
                createDayContent(2L, Category.카오스던전, "타락2", 1445, 73779, 2919, 5.6, 82.2, 241.0, 8),
                createDayContent(3L, Category.카오스던전, "타락3", 1475, 75378, 2897, 6.6, 89.1, 268.4, 8),
                createDayContent(4L, Category.카오스던전, "공허1", 1490, 76884, 5416, 3.1, 52.5, 149.9, 8),
                createDayContent(5L, Category.카오스던전, "공허2", 1520, 77565, 6885, 4.1, 63.4, 243.4, 10),
                createDayContent(6L, Category.카오스던전, "절망1", 1540, 81193, 8173, 4.8, 70.2, 207.7, 11),
                createDayContent(7L, Category.카오스던전, "절망2", 1560, 81859, 10006, 5.8, 78.2, 241.8, 10),
                createDayContent(8L, Category.카오스던전, "천공1", 1580, 80164, 9913, 3.1, 40.7, 110.0, 10),
                createDayContent(9L, Category.카오스던전, "천공2", 1600, 84164, 10128, 4.0, 44.1, 128.9, 11),
                createDayContent(10L, Category.카오스던전, "계몽1", 1610, 97382, 11198, 5.3, 60.0, 171.9, 11),
                createDayContent(91L, Category.카오스던전, "계몽2", 1630, 96918, 14349, 9.2, 95.3, 273.1, 12.3),
                createDayContent(110L, Category.카오스던전, "쿠르잔전선 1", 1640, 186048, 22008, 11.0, 129.5, 471.0, 4),
                createDayContent(111L, Category.카오스던전, "쿠르잔전선 2", 1660, 194048, 24008, 12.0, 175.0, 600.0, 4),
                createDayContent(141L, Category.카오스던전, "쿠르잔전선 3", 1680, 194048, 24008, 13.0, 200.0, 700.0, 5),

                // 가디언토벌
                createDayContent(11L, Category.가디언토벌, "데스칼루다", 1415, 0, 0, 10.7, 103.4, 310.6, 0),
                createDayContent(12L, Category.가디언토벌, "쿤겔라니움", 1460, 0, 0, 15.2, 131.9, 396.9, 0),
                createDayContent(13L, Category.가디언토벌, "칼엘리고스", 1490, 0, 0, 10.1, 74.3, 222.6, 0),
                createDayContent(14L, Category.가디언토벌, "하누마탄", 1540, 0, 0, 14.0, 101.4, 309.5, 0),
                createDayContent(15L, Category.가디언토벌, "소나벨", 1580, 0, 0, 8.0, 66.3, 199.7, 0),
                createDayContent(16L, Category.가디언토벌, "가르가디스", 1610, 0, 0, 12.0, 103.7, 311.1, 0),
                createDayContent(92L, Category.가디언토벌, "베스칼", 1630, 0, 0, 24.0, 168.3, 506.5, 0),
                createDayContent(109L, Category.가디언토벌, "아게오로스", 1640, 0, 0, 12.0, 94.6, 293.0, 0),
                createDayContent(142L, Category.가디언토벌, "스콜라키아", 1680, 0, 0, 19.0, 195.0, 434.0, 0)
        );
    }

    private static DayContent createDayContent(
            Long id,
            Category category,
            String name,
            double level,
            double shilling,
            double honorShard,
            double leapStone,
            double destructionStone,
            double guardianStone,
            double jewelry) {
        return DayContent.builder()
                .id(id)
                .category(category)
                .name(name)
                .level(level)
                .shilling(shilling)
                .honorShard(honorShard)
                .leapStone(leapStone)
                .destructionStone(destructionStone)
                .guardianStone(guardianStone)
                .jewelry(jewelry)
                .build();
    }
}
