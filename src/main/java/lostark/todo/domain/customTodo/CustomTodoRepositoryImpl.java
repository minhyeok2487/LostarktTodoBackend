package lostark.todo.domain.customTodo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.member.entity.Member;
import java.util.List;
import static lostark.todo.domain.customTodo.QCustomTodo.customTodo;
import static lostark.todo.domainV2.member.entity.QMember.member;
import static lostark.todo.domainV2.character.entity.QCharacter.character;

@RequiredArgsConstructor
public class CustomTodoRepositoryImpl implements CustomTodoCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public List<CustomTodo> search(String username) {
        return factory.select(customTodo)
                .from(customTodo)
                .leftJoin(customTodo.character, character).fetchJoin()
                .leftJoin(character.member, member).fetchJoin()
                .where(
                        member.username.eq(username)
                ).fetch();
    }

    @Override
    public long update(CustomTodoFrequencyEnum frequency) {
        return factory.update(customTodo)
                .set(customTodo.isChecked, false)
                .where(customTodo.frequency.eq(frequency))
                .execute();
    }

    @Override
    public void deleteByMember(Member member) {
        factory.delete(customTodo)
                .where(customTodo.character.in(member.getCharacters()))
                .execute();
    }
}
