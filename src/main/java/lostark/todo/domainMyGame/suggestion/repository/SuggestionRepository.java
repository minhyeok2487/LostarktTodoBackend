package lostark.todo.domainMyGame.suggestion.repository;

import lostark.todo.domainMyGame.suggestion.entity.UserSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository extends JpaRepository<UserSuggestion, Long> {
}
