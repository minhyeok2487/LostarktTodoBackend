package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GemDto {

    private String skillName;
    private int gemLevel;
    private String description;
    private String option;
    private String icon;
}
