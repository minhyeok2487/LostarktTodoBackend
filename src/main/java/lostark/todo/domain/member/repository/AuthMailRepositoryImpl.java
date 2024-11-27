package lostark.todo.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.member.entity.AuthMail;
import java.util.Optional;

import static lostark.todo.domain.member.entity.QAuthMail.authMail;


@RequiredArgsConstructor
public class AuthMailRepositoryImpl implements AuthMailCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public Optional<AuthMail> getAuthMail(String email, Integer number) {
        return Optional.ofNullable(factory.select(authMail)
                .from(authMail)
                .where(
                        authMail.mail.eq(email),
                        authMail.number.eq(number),
                        authMail.isAuth.isTrue()
                ).fetchFirst());
    }
}
