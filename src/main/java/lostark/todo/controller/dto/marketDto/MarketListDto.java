package lostark.todo.controller.dto.marketDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketListDto {

    private List<MarketDto> marketDtoList;
}
