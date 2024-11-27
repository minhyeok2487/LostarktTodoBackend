package lostark.todo.controller.dtoV2.character;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.entity.CustomTodo;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTodoResponse {

    private long customTodoId;

    private long characterId;

    private String contentName;

    private boolean isChecked;

    private CustomTodoFrequencyEnum frequency;

    public CustomTodoResponse(CustomTodo customTodo) {
        this.customTodoId = customTodo.getId();
        this.characterId = customTodo.getCharacter().getId();
        this.contentName = customTodo.getContentName();
        this.isChecked = customTodo.isChecked();
        this.frequency = customTodo.getFrequency();
    }
}
