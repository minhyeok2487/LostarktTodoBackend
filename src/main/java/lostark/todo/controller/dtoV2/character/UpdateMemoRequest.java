package lostark.todo.controller.dtoV2.character;

import lombok.*;
import lostark.todo.domain.character.dto.BaseCharacterRequest;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateMemoRequest extends BaseCharacterRequest {

    @Size(max = 100)
    private String memo;
}
