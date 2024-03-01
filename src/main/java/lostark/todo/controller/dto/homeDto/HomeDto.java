package lostark.todo.controller.dto.homeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeDto {

    private List<CharacterDto> characterDtoList;

    private CharacterDto mainCharacter;

    private List<HomeRaidDto> homeRaidDtoList;

    private double dayTotalGold;

    private double weekTotalGold;
}
