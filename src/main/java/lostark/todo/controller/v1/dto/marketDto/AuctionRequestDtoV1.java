package lostark.todo.controller.v1.dto.marketDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRequestDtoV1 {

    private String username;

    private String itemName;

    private int categoryCode;
}
