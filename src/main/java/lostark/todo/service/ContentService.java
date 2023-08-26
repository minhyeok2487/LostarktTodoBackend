package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.*;
import lostark.todo.domain.todo.TodoContentName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;

    public List<Content> findAll() {
        return contentRepository.findAll();
    }

    /**
     * 일일숙제(카오스던전, 가디언토벌)데이터 호출
     */
    public Map<String, DayContent> findDayContent() {
        Map<String, DayContent> dayContentMap = new HashMap<>();
        for (DayContent dayContent : contentRepository.findDayContent()) {
            dayContentMap.put(dayContent.getName(), dayContent);
        }
        return dayContentMap;
    }

    public WeekContent save(WeekContent weekContent) {
        return contentRepository.save(weekContent);
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

            List<TodoResponseDto> todoResponseDtoList = character.getTodoList().stream()
                    .map(todo -> TodoResponseDto.builder()
                            .id(todo.getId())
                            .check(todo.isChecked())
                            .gold(todo.getGold())
                            .contentName(todo.getContentName().getDisplayName())
                            .build())
                    .collect(Collectors.toList());

            // character 엔티티로 dto 객체 생성
            CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
                    .id(character.getId())
                    .characterName(character.getCharacterName())
                    .characterImage(character.getCharacterImage())
                    .characterClassName(character.getCharacterClassName())
                    .itemLevel(character.getItemLevel())
                    .chaosCheck(character.getDayTodo().getChaosCheck())
                    .chaosGauge(character.getDayTodo().getChaosGauge())
//                    .chaosName(contentMap.get(Category.카오스던전))
                    .guardianCheck(character.getDayTodo().getGuardianCheck())
                    .guardianGauge(character.getDayTodo().getGuardianGauge())
//                    .guardianName(contentMap.get(Category.가디언토벌))
                    .eponaCheck(character.getDayTodo().isEponaCheck())
                    .todoList(todoResponseDtoList)
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
                DayContentProfitDto chaos = DayContentProfitDto.builder()
//                        .contentName(returnDto.getChaosName().getName())
                        .category("카오스던전")
                        .checked(returnDto.getChaosCheck())
                        .characterName(returnDto.getCharacterName())
                        .build();
                double chaosProfit = returnDto.getChaosGold();
                result.put(chaos, chaosProfit);

                DayContentProfitDto guardian = DayContentProfitDto.builder()
//                        .contentName(returnDto.getGuardianName().getName())
                        .category("가디언토벌")
                        .checked(returnDto.getGuardianCheck())
                        .characterName(returnDto.getCharacterName())
                        .build();
                double guardianProfit = returnDto.getGuardianGold();
                result.put(guardian, guardianProfit);

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

    public int findWeekGold(TodoContentName contentName) {
        return contentRepository.findWeekGold(contentName.getDisplayName(), contentName.getGate());
    }


}
