package lostark.todo.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;

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

    // 회원가입 생성자
    public Member(MemberSignupDto signupDto) {
        this.apiKey = signupDto.getApiKey();
        this.username = signupDto.getUsername();
        this.password = signupDto.getPassword();
        this.role = Role.USER;
    }

    // user 엔티티에 character 리스트 저장
    public Character addCharacter(Character character) {
        characters.add(character);
        character.setMember(this);
        return character;
    }

}
