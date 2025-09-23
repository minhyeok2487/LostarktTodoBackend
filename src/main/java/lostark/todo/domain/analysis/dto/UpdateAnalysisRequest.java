package lostark.todo.domain.analysis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lostark.todo.domain.character.dto.BaseCharacterRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateAnalysisRequest extends BaseCharacterRequest {

    @NotEmpty
    private String characterName;

    @NotEmpty
    private String contentName;

    @NotNull
    private Integer battleTime;

    @NotNull
    private Long damage;

    @NotNull
    private Long dps;

    private Map<String, Long> customData;
}