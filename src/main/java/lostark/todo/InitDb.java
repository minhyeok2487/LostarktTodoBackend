package lostark.todo;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
//        initService.dbMemberInit();
//        initService.dbContentInit();
    }

    @Transactional
    @Component
    @RequiredArgsConstructor
    static class InitService {

        private final MemberService memberService;
        private final LostarkCharacterService lostarkCharacterService;
        private final ContentService contentService;
        private final CharacterService characterService;

        public void dbMemberInit() {
            //회원가입
            String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDAwMDAxMzMifQ.btfajuzfvAHxcU500YOuF3v3NHS4I7tnV52nce-1R6ZfLM8CPDzfIW8InZBKv_SsSqbVq-_U3khNpAPPCIUXA-QCD2vPCMc42-PJXtwMjGVqaRayRJhtZxbiyL2Gqjpj8fm8GIymnBY1ODyU-JozgfA1Z9bfqrlhxseBfo6aB4BTLpPuAjQFAxpX2NaqeGEWhb3Dkm-Gqo6nMJg37qXsPSxKYNqY9BRHqx8sgHf9Mm6io3Z3Rvm9rx_2Qv0jhwhK2FMXslrqfB0717rXsLQMELfTIisbgP42x1kloKVTGUFm7tpNL2W0SLpIqxiroGW6GGdyiYWEvo4xVKXGmubvPw";
            String username = "qwe2487";
            String password = "123456";
            Member member = new Member(apiKey, username, password);
            memberService.signup(member);

            //캐릭터 추가
            try {
                lostarkCharacterService.characterInfoAndSave(username, "마볼링");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<String> characterList = new ArrayList<>();
            characterList.add("마볼링");
            characterList.add("카카오볼링");
            characterList.add("가을볼링");
            characterList.add("데이터볼링");
            characterList.add("볼링치는개발자");
            for (String s : characterList) {
                characterService.findCharacterByName(s).setSelected(true);
            }

        }

        public void dbContentInit() {
            DayContent chaosContent = DayContent.createChaos(72415, 2438,
                    4.9, 76.7, 226.4, 7, 21);
            contentService.saveDayContent(chaosContent, 1415, "타락1");

        }
    }
}

