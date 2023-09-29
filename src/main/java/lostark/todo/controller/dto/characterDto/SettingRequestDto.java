package lostark.todo.controller.dto.characterDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingRequestDto {

    private String characterName;

    private boolean value;

    private String name;
}
