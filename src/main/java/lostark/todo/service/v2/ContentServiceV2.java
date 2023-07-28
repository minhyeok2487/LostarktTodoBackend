package lostark.todo.service.v2;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDtoV2;
import lostark.todo.controller.v1.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.v1.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.v1.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
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

    public List<CharacterResponseDtoV2> calculateDayContent(List<Character> characterList) {
        //출력할 리스트
        List<CharacterResponseDtoV2> characterResponseDtoList = new ArrayList<>();

        for (Character character : characterList) {
            // 캐릭터 레벨에 따른 일일컨텐츠
            Map<Category, DayContent> contentMap = getDayContentByLevel(character.getItemLevel());

            // character 엔티티로 dto 객체 생성
            CharacterResponseDtoV2 characterResponseDto = CharacterResponseDtoV2.builder()
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
}
