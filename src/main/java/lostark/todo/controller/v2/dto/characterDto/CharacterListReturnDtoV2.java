package lostark.todo.controller.v2.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.v1.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.v2.dto.contentDto.SortedDayContentProfitDtoV2;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterListReturnDtoV2 {

    @ApiModelProperty(example = "기본 캐릭터 리스트")
    List<CharacterResponseDtoV2> characters = new ArrayList<>();

    @ApiModelProperty(example = "일일 컨텐츠 수익 합")
    Double sumDayContentProfit;

    @ApiModelProperty(example = "일일 컨텐츠 수익 순으로 정렬")
    List<SortedDayContentProfitDtoV2> sortedDayContentProfitDtoList = new ArrayList<>();
}
