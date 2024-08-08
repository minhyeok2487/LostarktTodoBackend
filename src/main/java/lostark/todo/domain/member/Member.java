package lostark.todo.domain.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.Role;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.comments.Comments;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.notification.Notification;
import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseTimeEntity {

    // 회원 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;

    @Column(length = 1000)
    private String apiKey;

    @Column(unique = true)
    private String username;

    private String authProvider;

    private String accessKey;

    private String password;

    private String mainCharacter;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonManagedReference
    private List<Character> characters;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonManagedReference
    private List<Comments> comments;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonManagedReference
    private List<Friends> friends;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonManagedReference
    private List<Boards> boards;

    @OneToMany(mappedBy = "receiver", cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonManagedReference
    private List<Notification> notifications;


    // 대표캐릭터 변경
    public void editMainCharacter(String mainCharacter) {
        this.mainCharacter = mainCharacter;
    }

    // user 엔티티에 character 리스트 저장
    public Character addCharacter(Character character) {
        characters.add(character);
        character.setMember(this);
        return character;
    }

    // 유저 전환(구글 로그인 -> 일반 로그인)
    public Member changeAuthToNone(String encodePassword) {
        this.authProvider = "none";
        this.password = encodePassword;
        return this;
    }

    public void updatePassword(String encodePassword) {
        this.password = encodePassword;
    }
}
