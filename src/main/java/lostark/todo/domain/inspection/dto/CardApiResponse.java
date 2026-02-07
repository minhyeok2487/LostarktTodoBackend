package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardApiResponse {

    private List<CardDto> cards;
    private List<CardSetEffectDto> cardSetEffects;
}
