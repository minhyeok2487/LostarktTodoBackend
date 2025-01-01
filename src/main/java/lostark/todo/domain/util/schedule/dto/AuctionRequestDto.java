package lostark.todo.domain.util.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuctionRequestDto {

    private int ItemTier;

    private int CategoryCode;

    private String ItemName;
}