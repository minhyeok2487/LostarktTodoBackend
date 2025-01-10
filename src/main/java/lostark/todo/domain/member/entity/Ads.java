package lostark.todo.domain.member.entity;

import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Ads extends BaseTimeEntity {

    // 이메일 인증 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ads_id")
    private long id;

    private String name;

    private String proposerEmail;

    private long memberId;

    private boolean check;
}
