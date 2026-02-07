package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArkPassiveDto {

    private String category;
    private String name;
    private int level;
    private String icon;
    private String description;
}
