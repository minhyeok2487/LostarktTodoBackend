package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArkPassiveApiResponse {

    private String pointsJson;
    private List<ArkPassiveDto> effects;
}
