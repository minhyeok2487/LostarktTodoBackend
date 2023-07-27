package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Data
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

    @Column(unique = true)
    private String characterName;

    @Column(nullable = false)
    private int characterLevel; //전투레벨

    @Column(nullable = false)
    private String characterClassName; //캐릭터 클래스

    @Column(nullable = false)
    private String characterImage; //캐릭터 이미지 url

    @Column(nullable = false)
    private double itemLevel; //아이템레벨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    @Column(nullable = false)
    private boolean selected; //true면 출력할 캐릭(디폴트 true)

    @Embedded
    private CharacterDayContent characterDayContent;


}
