package lostark.todo.domain.member;

public interface MemberCustomRepository {
    Member findMemberAndMainCharacter(String username);
}
