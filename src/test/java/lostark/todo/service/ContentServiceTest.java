package lostark.todo.service;

import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.DayContent;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootTest
@Transactional
class ContentServiceTest {

    @Autowired ContentService contentService;
    @Autowired
    ContentRepository contentRepository;
    @Autowired MemberService memberService;
    @Autowired MarketService marketService;

    @Test
    public void saveDayContentTest() {
        DayContent chaosContent = DayContent.createChaos(72415, 2438,
                4.9, 76.7, 226.4, 7, 21);
        DayContent savedDayContent = contentService.saveDayContent(chaosContent, 1415, "타락1");

        DayContent content = contentService.findDayContentById(savedDayContent.getId());
        Assertions.assertThat(savedDayContent).isEqualTo(content);
    }

    @Test
    public void getDayContentByLevelTest() {
        double level = 1618.5;
        List<DayContent> content = contentRepository.findDayContentByLevel(level, Category.카오스던전);
        System.out.println("content = " + content.toString());
    }

    @Test
    public void todoTest() {
        String username = "qwe2487";
        try {
            List<Character> characterList = memberService.findMember(username).getCharacters();

            // 거래소 데이터 가져옴(Map)
            List<String> dayContentResource = marketService.dayContentResource();
            Map<String , MarketContentResourceDto> contentResource = marketService.getContentResource(dayContentResource);

            // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
            List<CharacterReturnDto> characterReturnDtoList = contentService.calculateDayContent(characterList, contentResource);

            // 모두 더하기
            double sum = 0;
            for (CharacterReturnDto returnDto : characterReturnDtoList) {
                sum += returnDto.getChaosProfit();
                sum += returnDto.getGuardianProfit();
            }

            // 순서 구하기
            Map<DayContentProfitDto, Double> result = new HashMap<>();
            for (CharacterReturnDto returnDto : characterReturnDtoList) {
//                DayContentProfitDto guardian = new DayContentProfitDto(returnDto.getCharacterName(), Category.가디언토벌, returnDto.getGuardianName());
//                double guardianProfit = returnDto.getGuardianProfit();
//                result.put(guardian, guardianProfit);
//
//                DayContentProfitDto chaos = new DayContentProfitDto(returnDto.getCharacterName(), Category.카오스던전,returnDto.getChaosName());
//                double chaosProfit = returnDto.getChaosProfit();
//                result.put(chaos, chaosProfit);
            }
            List<DayContentProfitDto> listKeySet = new ArrayList<>(result.keySet());
            JSONArray jsonArray = new JSONArray();
            Collections.sort(listKeySet, (value1, value2) -> (result.get(value2).compareTo(result.get(value1))));
            for(DayContentProfitDto key : listKeySet) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("characterName", key.getCharacterName());
                jsonObject.put("category", key.getCategory());
                jsonObject.put("contentName", key.getContentName());
                jsonObject.put("profit", result.get(key));
                jsonArray.add(jsonObject);
            }
            JSONObject resultObject = new JSONObject();
            resultObject.put("characters", characterReturnDtoList);
            resultObject.put("sumDayContentProfit", sum);
            resultObject.put("sortDayContentProfit", jsonArray);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}