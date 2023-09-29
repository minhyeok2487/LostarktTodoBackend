package lostark.todo.controller.dto.characterDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.Settings;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterSettingDto {

    private long id;

    private String serverName;

    private String characterName;

    private int characterLevel; //전투레벨

    private String characterClassName; //캐릭터 클래스

    private String characterImage; //캐릭터 이미지 url

    private double itemLevel; //아이템레벨

    private int sortNumber; //정렬용

    private boolean showCharacter;

    private boolean showEpona;

    private boolean showChaos;

    private boolean showGuardian;

    private boolean showWeekTodo;

    public static CharacterSettingDto toDto(Character entity) {
        return CharacterSettingDto.builder()
                .id(entity.getId())
                .serverName(entity.getServerName())
                .characterName(entity.getCharacterName())
                .characterLevel(entity.getCharacterLevel())
                .characterClassName(entity.getCharacterClassName())
                .characterImage(entity.getCharacterImage())
                .itemLevel(entity.getItemLevel())
                .sortNumber(entity.getSortNumber())
                .showCharacter(entity.getSettings().isShowCharacter())
                .showEpona(entity.getSettings().isShowEpona())
                .showChaos(entity.getSettings().isShowChaos())
                .showGuardian(entity.getSettings().isShowGuardian())
                .showWeekTodo(entity.getSettings().isShowWeekTodo())
                .build();
    }
}
