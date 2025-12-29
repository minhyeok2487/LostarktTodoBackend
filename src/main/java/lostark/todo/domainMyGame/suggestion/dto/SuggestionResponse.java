package lostark.todo.domainMyGame.suggestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainMyGame.suggestion.entity.UserSuggestion;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public static SuggestionResponse from(UserSuggestion suggestion) {
        return SuggestionResponse.builder()
                .id(suggestion.getId())
                .content(suggestion.getContent())
                .createdAt(suggestion.getCreatedDate())
                .build();
    }
}
