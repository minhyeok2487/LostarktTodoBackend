package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todoV2.TodoV2;
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

    /**
     * 캐릭터 정보 업데이트
     */
    public Character updateCharacter(Character updatedCharacter) {
        this.characterName = updatedCharacter.getCharacterName();
        this.characterLevel = updatedCharacter.getCharacterLevel();
        this.characterClassName = updatedCharacter.getCharacterClassName();
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

    public void updateChallenge(ChallengeContentEnum content) {
        if (content == ChallengeContentEnum.Guardian) {
            this.challengeGuardian = !this.challengeGuardian;
        }
        if (content == ChallengeContentEnum.Abyss) {
            this.challengeAbyss = !this.challengeAbyss;
        }
    }

    public void changeCharacter(Character before) {
        this.sortNumber = before.getSortNumber();
        this.dayTodo.setEponaGauge(before.getDayTodo().getEponaGauge());
        this.dayTodo.setEponaCheck2(before.getDayTodo().getEponaCheck2());
        this.dayTodo.setChaosGauge(before.getDayTodo().getChaosGauge());
        this.dayTodo.setChaosCheck(before.getDayTodo().getChaosCheck());
        this.dayTodo.setGuardianGauge(before.getDayTodo().getGuardianGauge());
        this.dayTodo.setGuardianCheck(before.getDayTodo().getGuardianCheck());
        this.weekTodo.setWeekEpona(before.getWeekTodo().getWeekEpona());
        this.weekTodo.setSilmaelChange(before.getWeekTodo().isSilmaelChange());
        this.weekTodo.setCubeTicket(before.getWeekTodo().getCubeTicket());
        this.todoList = before.getTodoList();
        this.todoV2List = before.getTodoV2List();
        this.goldCharacter = before.isGoldCharacter();
        this.challengeAbyss = before.isChallengeAbyss();
        this.challengeGuardian = before.isChallengeGuardian();
        this.settings = before.getSettings();
    }

    /**
     * 주간 컨텐츠 초기화
     */
    public void resetWeek() {
        // 주간 레이드
        for (TodoV2 todoV2 : this.todoV2List) {
            WeekContent weekContent = todoV2.getWeekContent();
            if(weekContent.getCoolTime()==2){ //2주 레이드(아브렐4, 카멘4관문)
                if(todoV2.getCoolTime()==2) { //2주구간 그대로인데
                    if(todoV2.isChecked()) { //체크면 주기 0으로 변경
                        todoV2.setCoolTime(0);
                    } else { //체크가 안되면 주기 1로 변경
                        todoV2.setCoolTime(1);
                    }
                }
                else { //2주 쿨타임이 아니라면 (0or1) -> 격주임
                    todoV2.setCoolTime(2); // 쿨타임 다시 2로 변경
                }
            }
            todoV2.setChecked(false); //나머지는 그냥 false
        }

        this.setChallengeAbyss(false); //도전 어비스 던전
        this.setChallengeGuardian(false); //도전 가디언 토벌
        this.getWeekTodo().setWeekEpona(0); //주간에포나
        this.getWeekTodo().setSilmaelChange(false); //실마엘 혈석교환
    }

    public void updateImageUrl(String characterimageUrl) {
        this.characterImage = characterimageUrl;
    }
}
