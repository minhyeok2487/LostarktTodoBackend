package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    //JSONObject로 만드는 생성자
    public Character(JSONObject jsonObject) {
        characterName = jsonObject.get("CharacterName").toString();
        characterLevel = Integer.parseInt(jsonObject.get("CharacterLevel").toString());
        characterClassName = jsonObject.get("CharacterClassName").toString();
        serverName = jsonObject.get("ServerName").toString();
        itemLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",",""));
    }
}
