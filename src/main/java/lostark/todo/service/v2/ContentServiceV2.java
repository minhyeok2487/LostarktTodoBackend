package lostark.todo.service.v2;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.Content;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.DayContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentServiceV2 {

    private final ContentRepository contentRepository;

    public List<Content> findAll() {
        return contentRepository.findAll();
    }

    public List<DayContent> findAllDayContent() {
        return contentRepository.findAllDayContent();
    }

    public List<CharacterResponseDto> getCharacterListWithDayContent(List<Character> characterList) {
        //출력할 리스트
        List<CharacterResponseDto> characterResponseDtoList = new ArrayList<>();

        for (Character character : characterList) {
            // 캐릭터 레벨에 따른 일일컨텐츠
            Map<Category, DayContent> contentMap = getDayContentByLevel(character.getItemLevel());

            // character 엔티티로 dto 객체 생성
            CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
                    .id(character.getId())
                    .characterName(character.getCharacterName())
                    .characterImage(character.getCharacterImage())
                    .characterClassName(character.getCharacterClassName())
                    .itemLevel(character.getItemLevel())
                    .chaosSelected(character.getCharacterDayContent().isChaosSelected())
                    .chaosCheck(character.getCharacterDayContent().getChaosCheck())
                    .chaosGauge(character.getCharacterDayContent().getChaosGauge())
                    .chaosName(contentMap.get(Category.카오스던전))
                    .guardianSelected(character.getCharacterDayContent().isGuardianSelected())
                    .guardianCheck(character.getCharacterDayContent().getGuardianCheck())
                    .guardianGauge(character.getCharacterDayContent().getGuardianGauge())
                    .guardianName(contentMap.get(Category.가디언토벌))
                    .build();

            characterResponseDtoList.add(characterResponseDto);
            }
        return characterResponseDtoList;
    }

    /**
     * 캐릭터 레벨에 따른 일일컨텐츠
     */
    public Map<Category, DayContent> getDayContentByLevel(double level) {
        DayContent chaosContent = contentRepository.findDayContentByLevel(level, Category.카오스던전).get(0);
        DayContent guardianContent = contentRepository.findDayContentByLevel(level, Category.가디언토벌).get(0);

        Map<Category, DayContent> dayContentMap = new HashMap<>();
        dayContentMap.put(Category.카오스던전, chaosContent);
        dayContentMap.put(Category.가디언토벌, guardianContent);
        return dayContentMap;
    }

    /**
     * 수익순으로 내림차순 정렬 메소드
     */
    public List<SortedDayContentProfitDto> sortDayContentProfit(List<CharacterResponseDto> characterResponseDtoList) {
        Map<DayContentProfitDto, Double> result = new HashMap<>();
        for (CharacterResponseDto returnDto : characterResponseDtoList) {
            if (returnDto.isChaosSelected()) {
                DayContentProfitDto chaos = DayContentProfitDto.builder()
                        .contentName(returnDto.getChaosName().getName())
                        .category("카오스던전")
                        .checked(returnDto.getChaosCheck())
                        .characterName(returnDto.getCharacterName())
                        .build();
                double chaosProfit = returnDto.getChaosProfit();
                result.put(chaos, chaosProfit);
            }

            if (returnDto.isGuardianSelected()) {
                DayContentProfitDto guardian = DayContentProfitDto.builder()
                        .contentName(returnDto.getGuardianName().getName())
                        .category("가디언토벌")
                        .checked(returnDto.getGuardianCheck())
                        .characterName(returnDto.getCharacterName())
                        .build();
                double guardianProfit = returnDto.getGuardianProfit();
                result.put(guardian, guardianProfit);
            }
        }
        List<DayContentProfitDto> listKeySet = new ArrayList<>(result.keySet());
        Collections.sort(listKeySet, (value1, value2) -> (result.get(value2).compareTo(result.get(value1))));
        List<SortedDayContentProfitDto> dtoList = new ArrayList<>();
        for(DayContentProfitDto key : listKeySet) {
            SortedDayContentProfitDto dto = SortedDayContentProfitDto.builder()
                    .characterName(key.getCharacterName())
                    .category(key.getCategory())
                    .contentName(key.getContentName())
                    .checked(key.getChecked())
                    .profit(result.get(key))
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }


}
