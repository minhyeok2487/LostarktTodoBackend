package lostark.todo.service;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

//    @Test
//    void addCharactersTest() {
//        //회원가입
//        String apiKey = "TestAPI";
//        Member member = new Member(apiKey);
//        Member signupMember = userService.signup(member);
//
//        //캐릭터 추가
//        List<Character> characters = new ArrayList<>();
//        characters.add(new Character("마볼링1", 1618.5));
//        characters.add(new Character("마볼링2", 1619.5));
//        characters.add(new Character("마볼링3", 1620.5));
//        characters.add(new Character("마볼링4", 1621.5));
//
//        try {
//            Member addCharactersMember = userService.addCharacters(signupMember, characters);
//            Member findMember = userService.findUser(addCharactersMember.getId());
//
//            Assertions.assertThat(findMember).isEqualTo(signupMember);
//            Assertions.assertThat(findMember.getCharacters().size()).isEqualTo(4);
//            Assertions.assertThat(findMember.getCharacters().get(0).getName()).isEqualTo("마볼링1");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    @Test
    void findMemberTest() {
        String username = "qwe2487";

        Member member = memberRepository.findByUsernameSelected(username);

        for (Character character : member.getCharacters()) {
            System.out.println("character.getCharacterName() = " + character.getCharacterName());
        }

    }
}