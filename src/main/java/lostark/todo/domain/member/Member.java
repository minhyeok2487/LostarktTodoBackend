package lostark.todo.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    /**
     * 회원 목록 테이블
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;

    @Column(unique = true, nullable = false, length = 1000)
    private String apiKey;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Character> characters = new ArrayList<>();

    /**
     * 연관 관계 메서드
     */

    // userApiKey 생성자
    public Member(String apiKey) {
        this.apiKey = apiKey;
    }

    public Member(String apiKey, String username, String password) {
        this.apiKey = apiKey;
        this.username = username;
        this.password = password;
        this.role = Role.USER;
    }

    // user 엔티티에 character 리스트 저장
    public void addCharacter(Character character) {
        characters.add(character);
        character.setMember(this);
    }

}
