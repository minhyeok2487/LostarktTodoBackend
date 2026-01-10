package lostark.todo.domain.character.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.character.dto.CharacterUpdateContext;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.member.entity.Member;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "characters", indexes = {
        @Index(name = "idx_characters_created_date", columnList = "createdDate"),
        @Index(name = "idx_characters_deleted_member", columnList = "isDeleted, member_id")
})
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

    @Column(nullable = false)
    private double combatPower; //전투력

    private int sortNumber; //정렬용

    @Column(length = 100)
    private String memo; //캐릭터 별 메모

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    @Embedded
    private DayTodo dayTodo;

    @Embedded WeekTodo weekTodo;

    @OneToMany(mappedBy = "character", cascade = {CascadeType.ALL})
    @JsonManagedReference
    private List<TodoV2> todoV2List;

    @OneToMany(mappedBy = "character", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<RaidBusGold> raidBusGoldList;

    private boolean goldCharacter; //골드 획득 지정 캐릭터

    private boolean challengeGuardian; //도전 가디언 토벌

    private boolean challengeAbyss; //도전 어비스 던전

    @Embedded
    private Settings settings;

    @ColumnDefault("false")
    private boolean isDeleted;


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
                ", sortNumber=" + sortNumber +
                ", characterDayContent=" + dayTodo +
                '}';
    }

    public Character toEntity(Member member, CharacterUpdateContext characterUpdateContext) {
        CharacterJsonDto newCharacter = characterUpdateContext.getNewCharacter();
        return Character.builder()
                .serverName(newCharacter.getServerName())
                .characterName(newCharacter.getCharacterName())
                .characterLevel(newCharacter.getCharacterLevel())
                .characterClassName(newCharacter.getCharacterClassName())
                .characterImage(newCharacter.getCharacterImage())
                .itemLevel(newCharacter.getItemAvgLevel())
                .combatPower(newCharacter.getCombatPower())
                .sortNumber(99999)
                .memo(null)
                .member(member)
                .dayTodo(characterUpdateContext.getDayTodo())
                .weekTodo(new WeekTodo())
                .todoV2List(new ArrayList<>())
                .raidBusGoldList(new ArrayList<>())
                .goldCharacter(false)
                .settings(new Settings())
                .isDeleted(false)
                .build();
    }

    public void updateGoldCharacter() {
        this.goldCharacter = !this.goldCharacter;
    }

    public void updateCharacter(CharacterUpdateContext updateContext) {
        this.characterName = updateContext.getNewCharacter().getCharacterName();
        this.characterLevel = updateContext.getNewCharacter().getCharacterLevel();
        this.characterClassName = updateContext.getNewCharacter().getCharacterClassName();
        this.characterImage = updateContext.getNewCharacter().getCharacterImage();
        this.serverName = updateContext.getNewCharacter().getServerName();
        this.itemLevel = updateContext.getNewCharacter().getItemAvgLevel();
        this.combatPower = updateContext.getNewCharacter().getCombatPower();
        this.dayTodo.setChaosName(updateContext.getDayTodo().getChaosName());
        this.dayTodo.setChaos(updateContext.getDayTodo().getChaos());
        this.dayTodo.setGuardianName(updateContext.getDayTodo().getGuardianName());
        this.dayTodo.setGuardian(updateContext.getDayTodo().getGuardian());
        this.dayTodo.calculateDayTodo(this, updateContext.getContentResource());
    }

    public void updateMemo(String memo) {
        this.memo = StringUtils.hasText(memo) ? memo : null;
    }

    public void updateCharacterStatus() {
        // 1. 골득 획득 캐릭터면 골득 획득 해제
        if (this.goldCharacter) {
            this.goldCharacter = false;
        }

        this.isDeleted = !this.isDeleted;
    }

    public void updateCharacterName(String newCharacterName) {
        if (this.characterName.equals(this.getMember().getMainCharacter())) {
            this.member.setMainCharacter(newCharacterName);
        }
        this.characterName = newCharacterName;
    }

    public void updateByAdmin(String characterName, Double itemLevel, Integer sortNumber,
                              String memo, Boolean goldCharacter, Boolean isDeleted) {
        if (characterName != null) {
            this.characterName = characterName;
        }
        if (itemLevel != null) {
            this.itemLevel = itemLevel;
        }
        if (sortNumber != null) {
            this.sortNumber = sortNumber;
        }
        if (memo != null) {
            this.memo = memo.isEmpty() ? null : memo;
        }
        if (goldCharacter != null) {
            this.goldCharacter = goldCharacter;
        }
        if (isDeleted != null) {
            this.isDeleted = isDeleted;
        }
    }
}
