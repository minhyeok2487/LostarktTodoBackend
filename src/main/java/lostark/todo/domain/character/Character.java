package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Entity
@Data
@Table(name = "characters")
public class Character extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "characters_id")
    private long id;

    @NotNull
    private String serverName;

    @NotNull
    private String characterName;

    @NotNull
    private int characterLevel; //전투레벨

    @NotNull
    private String characterClassName; //캐릭터 클래스

    @NotNull
    private double itemLevel; //아이템레벨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    @NotNull
    private boolean selected; //true면 출력할 캐릭(디폴트 true)

    @Embedded
    private CharacterContent characterContent;

    //초기 JSONObject로 만드는 생성자
    public Character(JSONObject jsonObject) {
        characterName = jsonObject.get("CharacterName").toString();
        characterLevel = Integer.parseInt(jsonObject.get("CharacterLevel").toString());
        characterClassName = jsonObject.get("CharacterClassName").toString();
        serverName = jsonObject.get("ServerName").toString();
        itemLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",",""));
        selected = true;
        characterContent = new CharacterContent(); //기본 생성자 (true, 0, 0, true, 0, 0)
    }

    protected Character() {}

    public Character changeSelected() {
        this.selected = !selected;
        return this;
    }

    public void changeItemLevel(double itemMaxLevel) {
        this.itemLevel = itemMaxLevel;
    }
}
