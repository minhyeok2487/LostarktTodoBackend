package lostark.todo.controller.v1.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.controller.v1.dto.contentDto.SortedDayContentProfitDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class CharacterListReturnDto {

    @ApiModelProperty(example = "기본 캐릭터 리스트")
    List<CharacterResponseDto> characters = new ArrayList<>();

    @ApiModelProperty(example = "일일 컨텐츠 수익 합")
    Double sumDayContentProfit;

    @ApiModelProperty(example = "일일 컨텐츠 수익 순으로 정렬")
    List<SortedDayContentProfitDto> sortedDayContentProfitDtoList = new ArrayList<>();
}
