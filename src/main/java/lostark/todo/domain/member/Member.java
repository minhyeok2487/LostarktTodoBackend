package lostark.todo.domain.member;

import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseTimeEntity {

    /**
     * 회원 목록 테이블
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;

    @Column(nullable = false, length = 1000)
    private String apiKey;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<Character> characters;


    // user 엔티티에 character 리스트 저장
    public Character addCharacter(Character character) {
        characters.add(character);
        character.setMember(this);
        return character;
    }
}
