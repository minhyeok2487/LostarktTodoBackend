package lostark.todo.domain.character.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.entity.CustomTodo;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.entity.QCustomTodo.customTodo;
import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.character.entity.QCharacter.character;
import static lostark.todo.global.exhandler.ErrorMessageConstants.CUSTOM_TODO_NOT_FOUND;

@RequiredArgsConstructor
public class CustomTodoRepositoryImpl implements CustomTodoCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public CustomTodo get(Long customTodoId, Long characterId) {
        return Optional.ofNullable(
                factory.selectFrom(customTodo)
                        .where(
                                eqId(customTodoId),
                                eqCharacterId(characterId)
                        )
                        .fetchOne()
        ).orElseThrow(() -> new NullPointerException(CUSTOM_TODO_NOT_FOUND));
    }

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

    private static BooleanExpression eqId(Long customTodoId) {
        if (customTodoId != null) {
            return customTodo.id.eq(customTodoId);
        }
        return null;
    }

    private static BooleanExpression eqCharacterId(Long characterId) {
        if (characterId != null) {
            return customTodo.character.id.eq(characterId);
        }
        return null;
    }
}
