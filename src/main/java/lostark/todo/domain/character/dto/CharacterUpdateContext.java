package lostark.todo.domain.character.dto;

import lombok.Data;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.util.content.enums.Category;
import lostark.todo.domain.util.market.entity.Market;

import java.util.List;
import java.util.Map;

@Data
public class CharacterUpdateContext {

    private CharacterJsonDto newCharacter;
    private DayTodo dayTodo;
    private Map<String, Market> contentResource;

    public CharacterUpdateContext(CharacterJsonDto newCharacter, Map<Category, List<DayContent>> dayContent
            , Map<String, Market> contentResource) {
        this.newCharacter = newCharacter;
        this.dayTodo = new DayTodo().createDayContent(dayContent, newCharacter.getItemMaxLevel());
        this.contentResource = contentResource;
    }
}
