package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInspectionCharacterRequest {

    @NotEmpty(message = "캐릭터 이름은 필수입니다.")
    private String characterName;

    private int noChangeThreshold = 3;
}
