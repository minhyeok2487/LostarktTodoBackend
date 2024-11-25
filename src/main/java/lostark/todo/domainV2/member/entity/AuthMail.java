package lostark.todo.domainV2.member.entity;

import lombok.*;
import lostark.todo.domain.BaseTimeEntity;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AuthMail extends BaseTimeEntity {

    // 이메일 인증 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_mail_id")
    private long id;

    private String mail;

    private Integer number;

    private boolean isAuth;

    public AuthMail(String mail, int number) {
        this.mail = mail;
        this.number = number;
        this.isAuth = false;
    }
}
