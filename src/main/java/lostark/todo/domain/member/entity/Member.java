package lostark.todo.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.domain.member.dto.SaveCharacterRequest;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.board.community.entity.Follow;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.board.comments.entity.Comments;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member", indexes = @Index(name = "idx_username", columnList = "username", unique = true))
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

    private LocalDateTime adsDate;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Character> characters;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Comments> comments;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Friends> friends;

    @OneToMany(mappedBy = "receiver", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Notification> notifications;

    @OneToMany(mappedBy = "follower", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Follow> followers;

    @OneToMany(mappedBy = "following", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Follow> following;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<LifeEnergy> lifeEnergyList = new ArrayList<>();

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

    // 광고 제거 기능 날짜 변경
    public void updateAdsDate(long donationPrice) {
        double daysPerUnitPrice = 30.0 / 200.0;
        long date = (long) Math.floor(donationPrice * daysPerUnitPrice) + 1;
        this.adsDate = Objects.requireNonNullElseGet(adsDate, LocalDateTime::now).plusDays(date);
    }

    // 이미 등록된 캐릭터인지 확인
    public void existCharacter(String characterName) {
        this.characters.stream()
                .filter(character -> character.getCharacterName().equals(characterName))
                .findFirst()
                .ifPresent(character -> {
                    throw new ConditionNotMetException("이미 등록된 캐릭터 이름입니다. 삭제된 캐릭터도 확인해주세요.");
                });
    }

    // CharacterList to Response Dto List
    public List<CharacterResponse> toDtoList() {
        return this.getCharacters().stream()
                .filter(c -> c.getSettings().isShowCharacter() && !c.isDeleted())
                .map(new CharacterResponse()::toDto)
                .sorted(Comparator
                        .comparingInt(CharacterResponse::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterResponse::getItemLevel).reversed()))
                .collect(Collectors.toList());
    }
}
