package lostark.todo.data;

import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberTestData {

    public static Member createMockMember(String username) {
        Member mockMember = Member.builder()
                .username(username)
                .apiKey(null)
                .mainCharacter(null)
                .build();
        mockMember.setCharacters(createMockCharacterList(mockMember));
        return mockMember;
    }

    public static Member createMockMember() {
        return createMockMember("test@test.com");
    }

    public static List<Character> createMockCharacterList(Member member) {
        List<Character> characterList = new ArrayList<>();
        Character mockCharacter1 = Character.builder()
                .characterName("테스트 캐릭터1")
                .characterLevel(70)
                .member(member)
                .itemLevel(1640.00)
                .dayTodo(createMockDayTodo())
                .settings(new Settings())
                .goldCharacter(true)
                .serverName("루페온")
                .build();
        Character mockCharacter2 = Character.builder()
                .characterName("테스트 캐릭터2")
                .characterLevel(70)
                .member(member)
                .itemLevel(1640.00)
                .dayTodo(createMockDayTodo())
                .settings(new Settings())
                .goldCharacter(true)
                .serverName("루페온")
                .build();
        Character mockCharacter3 = Character.builder()
                .characterName("테스트 캐릭터3")
                .characterLevel(70)
                .member(member)
                .itemLevel(1640.00)
                .dayTodo(createMockDayTodo())
                .settings(new Settings())
                .goldCharacter(true)
                .serverName("루페온")
                .build();
        Character mockCharacter4 = Character.builder()
                .characterName("테스트 캐릭터4")
                .characterLevel(70)
                .member(member)
                .itemLevel(1640.00)
                .dayTodo(createMockDayTodo())
                .settings(new Settings())
                .goldCharacter(true)
                .serverName("루페온")
                .build();
        Character mockCharacter5 = Character.builder()
                .characterName("테스트 캐릭터5")
                .characterLevel(70)
                .member(member)
                .itemLevel(1640.00)
                .dayTodo(createMockDayTodo())
                .settings(new Settings())
                .goldCharacter(true)
                .serverName("루페온")
                .build();
        Character mockCharacter6 = Character.builder()
                .characterName("테스트 캐릭터6")
                .characterLevel(70)
                .member(member)
                .itemLevel(1640.00)
                .dayTodo(createMockDayTodo())
                .settings(new Settings())
                .goldCharacter(false)
                .serverName("루페온")
                .build();
        Character mockCharacter7 = Character.builder()
                .characterName("테스트 캐릭터7")
                .characterLevel(70)
                .member(member)
                .itemLevel(1640.00)
                .dayTodo(createMockDayTodo())
                .settings(new Settings())
                .goldCharacter(false)
                .serverName("루페온")
                .build();
        characterList.add(mockCharacter1);
        characterList.add(mockCharacter2);
        characterList.add(mockCharacter3);
        characterList.add(mockCharacter4);
        characterList.add(mockCharacter5);
        characterList.add(mockCharacter6);
        characterList.add(mockCharacter7);
        return characterList;
    }

    public static DayTodo createMockDayTodo() {
        return DayTodo.builder().build();
    }
}
