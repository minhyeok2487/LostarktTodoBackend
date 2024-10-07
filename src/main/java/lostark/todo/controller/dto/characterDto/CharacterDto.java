package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.enums.goldCheckPolicy.GoldCheckPolicyEnum;
import lostark.todo.domainV2.character.entity.Settings;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.todoV2.TodoV2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterDto {

    private long id;

    @ApiModelProperty(notes = "캐릭터 클래스")
    private String characterClassName;

    @ApiModelProperty(notes = "캐릭터 이미지 url")
    private String characterImage;

    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @ApiModelProperty(notes = "캐릭터 아이템 레벨")
    private double itemLevel;

    @ApiModelProperty(notes = "서버 이름")
    private String serverName;

    @ApiModelProperty(notes = "정렬 번호")
    private int sortNumber;

    @ApiModelProperty(notes = "카오스던전 컨텐츠 이름 - 사용중지")
    private String chaosName;

    @ApiModelProperty(notes = "카오스 컨텐츠")
    private DayContent chaos;

    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private int chaosCheck;

    @ApiModelProperty(notes = "카오스던전 휴식게이지, 최소 0, 최대 100, 10단위 증가")
    private int chaosGauge;

    @ApiModelProperty(notes = "카오스던전 숙제 완료 시 예상 수익")
    private double chaosGold;

    @ApiModelProperty(notes = "가디언토벌 컨텐츠 이름 - 사용중지")
    private String guardianName;

    @ApiModelProperty(notes = "가디언토벌 컨텐츠")
    private DayContent guardian;

    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 1, 1씩 증감")
    private int guardianCheck;

    @ApiModelProperty(notes = "가디언토벌 휴식게이지, 최소 0, 최대 100, 10씩 증감")
    private int guardianGauge;

    @ApiModelProperty(notes = "가디언토벌 숙제 완료 시 예상 수익")
    private double guardianGold;

    @ApiModelProperty(notes = "에포나 의뢰 체크, 최소 0, 최대 3")
    private int eponaCheck;

    @ApiModelProperty(notes = "에포나 의뢰 휴식 게이지")
    private int eponaGauge;

    @ApiModelProperty(notes = "주간 숙제 리스트")
    private List<TodoResponseDto> todoList;

    @ApiModelProperty(notes = "골드획득 지정")
    private boolean goldCharacter;

    @ApiModelProperty(notes = "도전 가디언 토벌")
    private boolean challengeGuardian;

    @ApiModelProperty(notes = "도전 어비스 던전")
    private boolean challengeAbyss;

    @ApiModelProperty(notes = "캐릭터 설정")
    private Settings settings;

    @ApiModelProperty(notes = "주간 레이드 골드")
    private int weekGold;

    @ApiModelProperty(notes = "주간 에포나 체크, 최소 0, 최대 3")
    private int weekEpona;

    @ApiModelProperty(notes = "실마엘 교환 체크")
    private boolean silmaelChange;

    @ApiModelProperty(notes = "큐브 티켓 갯수")
    private int cubeTicket;

    public CharacterDto toDtoV2(Character character) {

        CharacterDto characterDto = CharacterDto.builder()
                .id(character.getId())
                .characterName(character.getCharacterName())
                .characterImage(character.getCharacterImage())
                .characterClassName(character.getCharacterClassName())
                .serverName(character.getServerName())
                .itemLevel(character.getItemLevel())
                .sortNumber(character.getSortNumber())
                .chaosCheck(character.getDayTodo().getChaosCheck())
                .chaosGauge(character.getDayTodo().getChaosGauge())
                .chaos(character.getDayTodo().getChaos())
                .chaosGold(character.getDayTodo().getChaosGold())
                .guardianCheck(character.getDayTodo().getGuardianCheck())
                .guardianGauge(character.getDayTodo().getGuardianGauge())
                .guardian(character.getDayTodo().getGuardian())
                .guardianGold(character.getDayTodo().getGuardianGold())
                .eponaCheck(character.getDayTodo().getEponaCheck2())
                .eponaGauge(character.getDayTodo().getEponaGauge())
                .goldCharacter(character.isGoldCharacter())
                .challengeAbyss(character.isChallengeAbyss())
                .challengeGuardian(character.isChallengeGuardian())
                .weekEpona(character.getWeekTodo().getWeekEpona())
                .silmaelChange(character.getWeekTodo().isSilmaelChange())
                .cubeTicket(character.getWeekTodo().getCubeTicket())
                .settings(character.getSettings())
                .build();

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        //주간레이드 entity -> dto
        if (!character.getTodoV2List().isEmpty()) {
            // dtoList 만들기
            // goldCheckVersion별 따로
            GoldCheckPolicyEnum goldCheckPolicyEnum = character.getSettings().getGoldCheckPolicyEnum();
            goldCheckPolicyEnum.getPolicy().calcTodoResponseDtoList(character, todoResponseDtoList);

            //sortNumber 순서대로 출력
            todoResponseDtoList.sort(Comparator.comparingInt(TodoResponseDto::getSortNumber));

            // 캐릭터 주간 레이드 수익 저장
            if (characterDto.isGoldCharacter()) {
                for (TodoV2 todo : character.getTodoV2List()) {
                    if (todo.isChecked()) {
                        String weekCategory = todo.getWeekContent().getWeekCategory();
                        int todoGold = todo.getGold();
                        for (TodoResponseDto todoResponseDto : todoResponseDtoList) {
                            if (weekCategory.equals(todoResponseDto.getWeekCategory()) && todoResponseDto.getGold() != 0) {
                                characterDto.setWeekGold(characterDto.getWeekGold() + todoGold);
                            }
                        }
                    }
                }
            }
        }

        //한 컨텐츠 완료 했는지 체크
        for (TodoResponseDto todoResponseDto : todoResponseDtoList) {
            if (todoResponseDto.getCurrentGate() == todoResponseDto.getTotalGate()) {
                todoResponseDto.setCheck(true);
            }
        }

        characterDto.setTodoList(todoResponseDtoList);

        return characterDto;
    }

    public List<CharacterDto> toDtoList(List<Character> characterList) {
        return characterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterDto().toDtoV2(character))
                .sorted(Comparator.comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                .toList();
    }

}
