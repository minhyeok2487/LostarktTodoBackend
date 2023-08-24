package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterGaugeDto;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todo.Todo;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

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

    @Embedded
    private CharacterDayContent characterDayContent;

    @OneToMany(mappedBy = "character", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<Todo> todoList;

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
                ", characterDayContent=" + characterDayContent +
                '}';
    }

    /**
     * 3가지 항목만 업데이트
     */
    public void updateCharacter(Character character) {
        this.characterLevel = character.getCharacterLevel();
        this.characterImage = character.getCharacterImage();
        this.itemLevel = character.getItemLevel();
    }

}
