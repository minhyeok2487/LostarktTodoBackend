package lostark.todo.domainMyGame.mygame.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainMyGame.mygame.entity.MyGame;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domainMyGame.mygame.entity.QMyGame.myGame;

@RequiredArgsConstructor
public class GameRepositoryImpl implements GameCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public MyGame get(String id) {
        return Optional.ofNullable(
                factory.selectFrom(myGame)
                        .where(myGame.id.eq(id))
                        .fetchOne()
        ).orElseThrow(() -> new ConditionNotMetException("게임을 찾을 수 없습니다."));
    }

    @Override
    public PageImpl<MyGame> searchGames(String search, PageRequest pageRequest) {
        List<MyGame> games = factory.selectFrom(myGame)
                .where(containsName(search))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(myGame.createdDate.desc())
                .fetch();

        long total = factory.selectFrom(myGame)
                .where(containsName(search))
                .fetchCount();

        return new PageImpl<>(games, pageRequest, total);
    }

    private BooleanExpression containsName(String search) {
        if (StringUtils.hasText(search)) {
            return myGame.name.contains(search);
        }
        return null;
    }
}
