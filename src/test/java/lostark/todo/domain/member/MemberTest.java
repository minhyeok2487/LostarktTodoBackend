package lostark.todo.domain.member;

import lostark.todo.domain.character.Character;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

//    @Test
//    void joinTest() throws Exception {
//        //given
//        String apiKey = "TestAPIsdadasdwdasdxcsdawseqwdasdxczxc";
//        Member member = new Member(apiKey);
//
//        List<Character> characterList = new ArrayList<>();
//        Character character = new Character("마볼링", 1618.5);
//        characterList.add(character);
//        member.addCharacters(characterList);
//
//        Member savedMember = memberRepository.save(member);
//
//        //when
//        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(() -> new Exception("존재하지않습니다"));
//        List<Character> findCharacters = findMember.getCharacters();
//
//        //then
//        Assertions.assertThat(findMember).isEqualTo(savedMember);
//        Assertions.assertThat(findCharacters.size()).isEqualTo(1);
//        Assertions.assertThat(findCharacters.get(0).getName()).isEqualTo(character.getName());
//    }
}