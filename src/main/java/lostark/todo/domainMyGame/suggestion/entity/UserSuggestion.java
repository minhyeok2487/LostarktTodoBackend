package lostark.todo.domainMyGame.suggestion.entity;

import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_suggestion")
public class UserSuggestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_suggestion_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
