package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GemDto {

    private String skillName;
    private int gemSlot;
    private String skillIcon;
    private int level;
    private String grade;
    private String description;
    private String gemOption;
}
