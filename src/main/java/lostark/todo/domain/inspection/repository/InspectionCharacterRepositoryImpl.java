package lostark.todo.domain.inspection.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.inspection.entity.InspectionCharacter;
import lostark.todo.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.inspection.entity.QInspectionCharacter.inspectionCharacter;
import static lostark.todo.domain.member.entity.QMember.member;

@RequiredArgsConstructor
public class InspectionCharacterRepositoryImpl implements InspectionCharacterCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<InspectionCharacter> findByMember(Member memberEntity) {
        return factory.selectFrom(inspectionCharacter)
                .leftJoin(inspectionCharacter.member, member).fetchJoin()
                .where(eqUsername(memberEntity.getUsername()))
                .orderBy(inspectionCharacter.createdDate.asc())
                .fetch();
    }

    @Override
    public Optional<InspectionCharacter> findByIdAndUsername(long id, String username) {
        InspectionCharacter result = factory.selectFrom(inspectionCharacter)
                .leftJoin(inspectionCharacter.member, member).fetchJoin()
                .where(
                        inspectionCharacter.id.eq(id),
                        eqUsername(username)
                )
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<InspectionCharacter> findActiveByScheduleHour(int hour) {
        return factory.selectFrom(inspectionCharacter)
                .leftJoin(inspectionCharacter.member, member).fetchJoin()
                .where(
                        inspectionCharacter.isActive.eq(true),
                        member.inspectionScheduleHour.eq(hour)
                )
                .fetch();
    }

    private BooleanExpression eqUsername(String username) {
        return inspectionCharacter.member.username.eq(username);
    }
}
