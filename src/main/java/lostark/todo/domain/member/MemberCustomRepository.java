package lostark.todo.domain.member;

public interface MemberCustomRepository {
    Member findMemberAndCharacters(String username);

    Member get(String username);
}
