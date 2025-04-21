package lostark.todo.domain.cube.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.cube.dto.CubeResponse;
import lostark.todo.domain.cube.entity.Cubes;
import lostark.todo.domain.cube.dto.QCubeResponse;
import org.springframework.util.StringUtils;

import java.util.List;

import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.character.entity.QCharacter.character;
import static lostark.todo.domain.cube.entity.QCubes.cubes;

@RequiredArgsConstructor
public class CubesRepositoryImpl implements CubesCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<CubeResponse> get(String username) {
        return factory.select(new QCubeResponse(cubes, character.id, character.characterName, character.itemLevel))
                .from(cubes)
                .join(character).on(character.id.eq(cubes.characterId))
                .join(member).on(member.id.eq(character.member.id))
                .where(
                        eqUsername(username)
                )
                .orderBy(character.sortNumber.asc())
                .fetch();
    }

    @Override
    public List<Cubes> searchByCharacterId(Long characterId) {
        return factory.selectFrom(cubes)
                .where(cubes.characterId.eq(characterId))
                .fetch();
    }

    @Override
    public void deleteAllByCharacterId(Long characterId) {
        if (characterId == null) {
            return;
        }
        factory.delete(cubes).where(cubes.characterId.eq(characterId)).execute();
    }

    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.eq(username);
        }
        return null;
    }
}
