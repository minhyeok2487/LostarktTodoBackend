package lostark.todo.domain.character.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateMemoRequest extends BaseCharacterRequest {

    @Size(max = 100)
    private String memo;
}
