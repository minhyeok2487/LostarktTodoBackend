package lostark.todo.domain.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.member.MemberResponse;
import lostark.todo.controller.dtoV2.member.QMainCharacterResponse;
import lostark.todo.controller.dtoV2.member.QMemberResponse;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.member.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory factory;

    @Override
    public MemberResponse findMemberResponse(String username) {
        return factory
                .select(new QMemberResponse(member.id, member.username,
                        new QMainCharacterResponse(
                                character.serverName, character.characterName,
                                character.characterImage, character.characterClassName, character.itemLevel)))
                .from(member)
                .leftJoin(character).on(character.member.eq(member))
                .where(character.characterName.eq(member.mainCharacter))
                .fetchOne();

    }
}
