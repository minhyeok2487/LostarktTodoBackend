package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todo.TodoContentName;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterTodoDto {

    @NotEmpty()
    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    private TodoContentName contentName;

}
