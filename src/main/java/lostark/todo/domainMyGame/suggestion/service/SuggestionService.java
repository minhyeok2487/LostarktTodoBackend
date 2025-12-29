package lostark.todo.domainMyGame.suggestion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainMyGame.suggestion.dto.SuggestionRequest;
import lostark.todo.domainMyGame.suggestion.dto.SuggestionResponse;
import lostark.todo.domainMyGame.suggestion.entity.UserSuggestion;
import lostark.todo.domainMyGame.suggestion.repository.SuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;

    @Transactional
    public SuggestionResponse createSuggestion(SuggestionRequest request) {
        UserSuggestion suggestion = request.toEntity();
        UserSuggestion savedSuggestion = suggestionRepository.save(suggestion);
        return SuggestionResponse.from(savedSuggestion);
    }
}
