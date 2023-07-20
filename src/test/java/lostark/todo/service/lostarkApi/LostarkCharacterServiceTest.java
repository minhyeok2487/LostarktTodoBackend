package lostark.todo.service.lostarkApi;

import lostark.todo.domain.member.MemberRepository;
import lostark.todo.service.MemberService;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class LostarkCharacterServiceTest {

    @Autowired
    LostarkCharacterService lostarkCharacterService;
    @Autowired
    MemberService memberService;


    @Test
    @DisplayName("로스트아크 캐릭터 정보 불러오기 성공")
    void characterInfoTest() {
        String apiKey = memberService.findMember("qwe2487").getApiKey();
        String characterName = "마볼링";
        JSONArray jsonArray = lostarkCharacterService.characterInfo(apiKey, characterName);
        for (int i = 0; i < jsonArray.size(); i++) {
            System.out.println("jsonArray = " + jsonArray.get(i).toString());
        }
    }
}