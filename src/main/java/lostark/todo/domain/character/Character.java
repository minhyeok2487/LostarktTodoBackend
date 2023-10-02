package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todo.Todo;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Table(name = "characters")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Character extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "characters_id")
    private long id;

    @Column(nullable = false)
    private String serverName;

    @Column(nullable = false)
    private String characterName;

    @Column(nullable = false)
    private int characterLevel; //전투레벨

    @Column(nullable = false)
    private String characterClassName; //캐릭터 클래스

    private String characterImage; //캐릭터 이미지 url

    @Column(nullable = false)
    private double itemLevel; //아이템레벨

    private int sortNumber; //정렬용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    @Embedded
    private DayTodo dayTodo;

    @OneToMany(mappedBy = "character", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<Todo> todoList;

    private boolean goldCharacter; //골드 획득 지정 캐릭터

    private boolean challengeGuardian; //도전 가디언 토벌

    private boolean challengeAbyss; //도전 어비스 던전

    @Embedded
    private Settings settings;

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", serverName='" + serverName + '\'' +
                ", characterName='" + characterName + '\'' +
                ", characterLevel=" + characterLevel +
                ", characterClassName='" + characterClassName + '\'' +
                ", characterImage='" + characterImage + '\'' +
                ", itemLevel=" + itemLevel +
                ", characterDayContent=" + dayTodo +
                '}';
    }

    /**
     * 캐릭터 정보 업데이트
     */
    public Character updateCharacter(Character updatedCharacter) {
        this.characterLevel = updatedCharacter.getCharacterLevel();
        this.characterImage = updatedCharacter.getCharacterImage();
        this.itemLevel = updatedCharacter.getItemLevel();
        this.dayTodo.setChaosName(updatedCharacter.getDayTodo().getChaosName());
        this.dayTodo.setChaos(updatedCharacter.getDayTodo().getChaos());
        this.dayTodo.setGuardianName(updatedCharacter.getDayTodo().getGuardianName());
        this.dayTodo.setGuardian(updatedCharacter.getDayTodo().getGuardian());
        return this;
    }

    public Character updateGoldCharacter() {
        this.goldCharacter = !this.goldCharacter;
        return this;
    }

    public Character createImage(Object characterImage) {
        if (characterImage != null) {
            this.characterImage = characterImage.toString();
        }
        return this;
    }

    public Character updateChallenge(String content) {
        if (content.equals("Guardian")) {
            this.challengeGuardian = !this.challengeGuardian;
        }
        if (content.equals("Abyss")) {
            this.challengeAbyss = !this.challengeAbyss;
        }
        return this;
    }
}
