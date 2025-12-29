package lostark.todo.domainMyGame.suggestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainMyGame.suggestion.entity.UserSuggestion;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionRequest {

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    public UserSuggestion toEntity() {
        return UserSuggestion.builder()
                .content(content)
                .build();
    }
}
