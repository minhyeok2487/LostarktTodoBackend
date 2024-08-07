package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todoV2.TodoV2;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;
import java.util.Map;


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
    private List<Todo> todoList;

    @OneToMany(mappedBy = "character", cascade = {CascadeType.ALL})
    @JsonManagedReference
    private List<TodoV2> todoV2List;

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
                ", sortNumber=" + sortNumber +
                ", characterDayContent=" + dayTodo +
                '}';
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

    public void updateChallenge(ChallengeContentEnum content) {
        if (content == ChallengeContentEnum.Guardian) {
            this.challengeGuardian = !this.challengeGuardian;
        }
        if (content == ChallengeContentEnum.Abyss) {
            this.challengeAbyss = !this.challengeAbyss;
        }
    }

    public void updateCharacter(CharacterJsonDto dto, DayTodo dayContent, Map<String, Market> contentResource) {
        this.characterName = dto.getCharacterName();
        this.characterLevel = dto.getCharacterLevel();
        this.characterClassName = dto.getCharacterClassName();
        this.characterImage = dto.getCharacterImage();
        this.itemLevel = dto.getItemMaxLevel();
        this.dayTodo.setChaosName(dayContent.getChaosName());
        this.dayTodo.setChaos(dayContent.getChaos());
        this.dayTodo.setGuardianName(dayContent.getGuardianName());
        this.dayTodo.setGuardian(dayContent.getGuardian());
        calculateDayTodo(this, contentResource);
    }

    public Character calculateDayTodo(Character character, Map<String, Market> contentResource) {
        Market jewelry = getJewelry(character.getItemLevel(), contentResource);
        Market destruction = getMarketItem(character.getItemLevel(), contentResource, "파괴석 결정", "파괴강석", "정제된 파괴강석", "운명의 파괴석");
        Market guardian = getMarketItem(character.getItemLevel(), contentResource, "수호석 결정", "수호강석", "정제된 수호강석", "운명의 수호석");
        Market leapStone = getMarketItem(character.getItemLevel(), contentResource, "위대한 명예의 돌파석", "경이로운 명예의 돌파석", "찬란한 명예의 돌파석", "운명의 돌파석");
        // 카오스 던전 계산
        calculateChaos(character.getDayTodo().getChaos(), destruction, guardian, jewelry, character);

        // 가디언 토벌 계산
        calculateGuardian(character.getDayTodo().getGuardian(), destruction, guardian, leapStone, character);
        return this;
    }

    private Market getJewelry(double itemLevel, Map<String, Market> contentResource) {
        if (itemLevel >= 1415 && itemLevel < 1640) {
            return contentResource.get("3티어 1레벨 보석");
        } else {
            return contentResource.get("4티어 1레벨 보석");
        }
    }

    private Market getMarketItem(double itemLevel, Map<String, Market> contentResource,
                                 String level1Item, String level2Item, String level3Item, String level4Item) {
        if (itemLevel >= 1415 && itemLevel < 1490) {
            return contentResource.get(level1Item);
        } else if (itemLevel >= 1490 && itemLevel < 1580) {
            return contentResource.get(level2Item);
        } else if (itemLevel >= 1580 && itemLevel < 1640) {
            return contentResource.get(level3Item);
        } else {
            return contentResource.get(level4Item);
        }
    }

    private void calculateChaos(DayContent dayContent, Market destruction, Market guardian, Market jewelry, Character character) {
        double price = 0;
        price += destruction.getRecentPrice() * dayContent.getDestructionStone() / destruction.getBundleCount();
        price += guardian.getRecentPrice() * dayContent.getGuardianStone() / guardian.getBundleCount();
        price += jewelry.getRecentPrice() * dayContent.getJewelry();

        int chaosGauge = character.getDayTodo().getChaosGauge();
        if (chaosGauge >= 40) {
            price = price*4;
        } else if (chaosGauge >= 20) {
            price = price*3;
        } else {
            price = price*2;
        }
        price = Math.round(price * 100.0) / 100.0;
        character.getDayTodo().setChaosGold(price);
    }

    private void calculateGuardian(DayContent dayContent, Market destruction, Market guardian, Market leapStone, Character character) {
        double price = 0;
        price += destruction.getRecentPrice() * dayContent.getDestructionStone() / destruction.getBundleCount();
        price += guardian.getRecentPrice() * dayContent.getGuardianStone() / guardian.getBundleCount();
        price += leapStone.getRecentPrice() * dayContent.getLeapStone() / leapStone.getBundleCount();

        int guardianGauge = character.getDayTodo().getGuardianGauge();
        if (guardianGauge >= 20) {
            price = price*2;
        }

        price = Math.round(price * 100.0) / 100.0;
        character.getDayTodo().setGuardianGold(price);
    }

    public Character updateMemo(String memo) {
        this.memo = StringUtils.hasText(memo) ? memo : null;
        return this;
    }

}
