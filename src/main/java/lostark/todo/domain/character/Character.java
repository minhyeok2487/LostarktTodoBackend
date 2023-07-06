package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lostark.todo.controller.dtos.CharacterSaveDto;
import lostark.todo.domain.member.Member;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "characters_id")
    private long id;

    private String serverName;

    private String characterName;

    private int characterLevel; //전투레벨

    private String characterClassName;

    private double itemLevel; //아이템레벨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    private boolean selected; //true면 출력할 캐릭(디폴트 false)

    private int chaos; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지

    private int guardian; //일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)

    private int guardianGauge; //가디언토벌 휴식게이지

    //JSONObject로 만드는 생성자
    public Character(JSONObject jsonObject) {
        characterName = jsonObject.get("CharacterName").toString();
        characterLevel = Integer.parseInt(jsonObject.get("CharacterLevel").toString());
        characterClassName = jsonObject.get("CharacterClassName").toString();
        serverName = jsonObject.get("ServerName").toString();
        itemLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",",""));
        selected = false;
    }

    public Character changeSelected() {
        if (this.selected == true) {
            this.selected = false;
        } else {
            this.selected = true;
        }
        return this;
    }

    public void update(CharacterSaveDto characterSaveDto) {
        this.chaos = characterSaveDto.getChaos();
        this.chaosGauge = characterSaveDto.getChaosGauge();
        this.guardian = characterSaveDto.getGuardian();
        this.guardianGauge = characterSaveDto.getGuardianGauge();
    }
}
