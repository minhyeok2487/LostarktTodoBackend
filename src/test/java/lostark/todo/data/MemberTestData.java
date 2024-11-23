package lostark.todo.data;

import lostark.todo.domainV2.member.entity.Member;

import java.util.ArrayList;

public class MemberTestData {

    public static Member createMockMember(String username) {
        return Member.builder()
                .username(username)
                .characters(new ArrayList<>())
                .apiKey(null)
                .mainCharacter(null)
                .build();
    }
}
