package lostark.todo.domain.member.repository;

import lostark.todo.domain.member.entity.AuthMail;

import java.util.Optional;

public interface AuthMailCustomRepository {
    Optional<AuthMail> getAuthMail(String email, Integer number);
}
