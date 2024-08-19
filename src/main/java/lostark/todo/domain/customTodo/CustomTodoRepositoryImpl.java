package lostark.todo.domain.customTodo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.customTodo.QCustomTodo.customTodo;
import static lostark.todo.domain.member.QMember.member;

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
}
