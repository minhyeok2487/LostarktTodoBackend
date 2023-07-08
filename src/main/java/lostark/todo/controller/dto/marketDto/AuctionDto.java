package lostark.todo.controller.dto.marketDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDto {

    private String itemName;

    private int categoryCode;
}
