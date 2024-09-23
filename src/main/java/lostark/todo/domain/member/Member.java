package lostark.todo.domain.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.Role;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.character.Character;
import lostark.todo.domainV2.board.comments.entity.Comments;
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

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Character> characters;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Comments> comments;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Friends> friends;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Boards> boards;

    @OneToMany(mappedBy = "receiver", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Notification> notifications;

    // 유저 전환(구글 로그인 -> 일반 로그인)
    public void changeAuthToNone(String encodePassword) {
        this.authProvider = "none";
        this.password = encodePassword;
    }

    // 대표캐릭터 변경
    public void editMainCharacter(String mainCharacter) {
        this.mainCharacter = mainCharacter;
    }


    // 회원가입 캐릭터 추가
    public void createCharacter(List<Character> characterList, SaveCharacterRequest request) {
        characterList.stream()
                .peek(character -> character.setMember(this))
                .forEach(characters::add);

        this.apiKey = request.getApiKey();
        this.mainCharacter = request.getCharacterName();
    }

    // 회원 API KEY 수정
    public void editApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    // 비밀번호 변경 - Test Code 작성
    public void updatePassword(String encodePassword) {
        this.password = encodePassword;
    }

    // 대표캐릭터 이름 찾기 (없으면 캐릭터 리스트 중 첫번째)
    public String getMainCharacterName() {
        return this.mainCharacter != null ? this.mainCharacter :
                this.characters.get(0).getCharacterName();
    }
}
