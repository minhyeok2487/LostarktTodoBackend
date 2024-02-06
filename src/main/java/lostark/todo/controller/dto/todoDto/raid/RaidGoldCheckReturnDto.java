package lostark.todo.controller.dto.todoDto.raid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaidGoldCheckReturnDto {

    List<WeekContentDto> weekContentDtoList;

    CharacterDto characterDto;
}
