package lostark.todo.domain.analysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Analysis extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "characters_id")
    @JsonBackReference
    private Character character;

    private double itemLevel;

    private double combatPower;

    private String contentName;

    private LocalDate contentDate;

    private int battleTime;

    private Long damage;

    private Long dps;
}
