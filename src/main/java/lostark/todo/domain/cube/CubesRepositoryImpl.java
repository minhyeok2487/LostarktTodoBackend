package lostark.todo.domain.cube;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.cube.CubeResponse;
import lostark.todo.controller.dtoV2.cube.QCubeResponse;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.cube.QCubes.*;
import static lostark.todo.domain.member.QMember.member;

@RequiredArgsConstructor
public class CubesRepositoryImpl implements CubesCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<CubeResponse> get(String username) {
        return factory.select(new QCubeResponse(character, cubes))
                .from(cubes)
                .join(character).on(character.id.eq(cubes.characterId))
                .join(member).on(member.id.eq(character.member.id))
                .where(
                        eqUsername(username)
                ).fetch();
    }

    @Override
    public Optional<Cubes> getByCharacterId(Long characterId) {
        Cubes fetchOne = factory.selectFrom(cubes)
                .where(cubes.characterId.eq(characterId))
                .fetchOne();
        return Optional.ofNullable(fetchOne);
    }

    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.eq(username);
        }
        return null;
    }
}
