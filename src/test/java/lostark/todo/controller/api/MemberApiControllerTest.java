package lostark.todo.controller.api;

import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.character.Settings;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberApiControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    ContentService contentService;

    @Autowired
    LostarkCharacterService lostarkCharacterService;

    @Autowired
    MarketService marketService;

    @Autowired
    CharacterService characterService;

    @Value("${Lostark-API-Test-Key}")
    String apiKey;

    @Test
    @DisplayName("회원가입시 캐릭터 추가 테스트 성공")
    void saveCharacterTest() {
        // given //
        String username = "qwe2487@ajou.ac.kr";
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .apiKey(apiKey)
                .username(username)
                .characterName("이다")
                .build();

        // when //
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> characterList = lostarkCharacterService.findCharacterList(memberDto.getCharacterName(), memberDto.getApiKey(), chaos, guardian);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : characterList) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // Member 회원가입
        Member signupMember = memberService.createCharacter(username, memberDto.getApiKey(), calculatedCharacterList);

        // 결과 출력
        MemberResponseDto responseDto = MemberResponseDto.builder()
                .id(signupMember.getId())
                .username(signupMember.getUsername())
                .characters(signupMember.getCharacters())
                .build();

        // then //
        Assertions.assertThat(responseDto.getCharacters().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("회원가입시 캐릭터 추가 테스트 성공 - 중복 캐릭터명 가능")
    void saveCharacterTestCharacterName() {
        // given //
        String username = "qwe2487@ajou.ac.kr";
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .apiKey(apiKey)
                .username(username)
                .characterName("마볼링")
                .build();

        // when //
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> characterList = lostarkCharacterService.findCharacterList(memberDto.getCharacterName(), memberDto.getApiKey(), chaos, guardian);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : characterList) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // Member 회원가입
        Member signupMember = memberService.createCharacter(username, memberDto.getApiKey(), calculatedCharacterList);

        // 결과 출력
        MemberResponseDto responseDto = MemberResponseDto.builder()
                .id(signupMember.getId())
                .username(signupMember.getUsername())
                .characters(signupMember.getCharacters())
                .build();

        // then //
        Assertions.assertThat(responseDto.getCharacters().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("회원가입시 캐릭터 추가 테스트 실패 - apikey에러")
    void saveCharacterTestExceptionApi() {
        // given //
        String username = "qwe2487@ajou.ac.kr";
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .apiKey(apiKey+"123")
                .username(username)
                .characterName("이다")
                .build();

        // when //
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // then //
        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        assertThatThrownBy(() -> lostarkCharacterService.findCharacterList(memberDto.getCharacterName(), memberDto.getApiKey(), chaos, guardian))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 apiKey 입니다");
    }

    @Test
    @DisplayName("회원가입시 캐릭터 추가 테스트 실패 - 없는 캐릭터명")
    void saveCharacterTestExceptionCharacter() {
        // given //
        String username = "qwe2487@ajou.ac.kr";
        String characterName = "asdnfubnlwnfjldndflnqwldnoqwnkdnlkasnclknxlckn";
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .apiKey(apiKey)
                .username(username)
                .characterName(characterName)
                .build();

        // when //
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // then //
        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        assertThatThrownBy(() -> lostarkCharacterService.findCharacterList(memberDto.getCharacterName(), memberDto.getApiKey(), chaos, guardian))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(characterName+" 은(는) 존재하지 않는 캐릭터 입니다.");
    }

    @Test
    @DisplayName("회원 캐릭터 업데이트 성공 - 캐릭터 추가")
    void updateCharacterListTestAddCharacter() {
        // given //
        String username = "repeat2487@gmail.com";
        Member member = memberService.findMember(username);
        final int beforeCharacterSize = member.getCharacters().size();

        // 대표캐릭터와 연동된 캐릭터(api 검증)
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
                member.getCharacters().get(0).getCharacterName(), member.getApiKey(), chaos, guardian);

        // 테스트용 새로운 캐릭터 추가
        Character newCharacter = Character.builder()
                .characterName("테스트용")
                .characterLevel(60)
                .characterClassName("소서리스")
                .serverName("루페온")
                .itemLevel(1655.0)
                .dayTodo(new DayTodo())
                .sortNumber(0)
                .build();
        newCharacter.setSettings(new Settings());
        newCharacter.setTodoList(new ArrayList<>());
        newCharacter.createImage("imageTest");
        newCharacter.getDayTodo().createDayContent(chaos, guardian, newCharacter.getItemLevel());
        updateCharacterList.add(0, newCharacter); //렙이 제일 높으니 리스트에 첫번째에 추가

        // when //
        // 변경된 내용 업데이트 및 추가, 삭제
        memberService.updateCharacterList(member, updateCharacterList);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : member.getCharacters()) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // 결과
        List<CharacterResponseDto> characterResponseDtoList = calculatedCharacterList.stream()
                .map(character -> new CharacterResponseDto().toDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator
                .comparingInt(CharacterResponseDto::getSortNumber)
                .thenComparing(Comparator.comparingDouble(CharacterResponseDto::getItemLevel).reversed())
        );


        // then //
        //업데이트 후 리스트 사이즈는 업데이트 전 리스트 사이즈보다 크다
        Assertions.assertThat(characterResponseDtoList.size()).isGreaterThan(beforeCharacterSize);
        //업데이트 후  리스트의 첫번째는 추가된 캐릭터이다.(아이템레벨이 제일 크기때문)
        Assertions.assertThat(characterResponseDtoList.get(0)).isEqualTo(new CharacterResponseDto().toDto(newCharacter));
    }

    @Test
    @DisplayName("회원 캐릭터 업데이트 성공 - 캐릭터 삭제")
    void updateCharacterListTestDeleteCharacter() {
        // given //
        String username = "repeat2487@gmail.com";
        Member member = memberService.findMember(username);
        final int beforeCharacterSize = member.getCharacters().size();

        // 대표캐릭터와 연동된 캐릭터(api 검증)
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
                member.getCharacters().get(0).getCharacterName(), member.getApiKey(), chaos, guardian);

        // 테스트용 새로운 캐릭터 삭제
        updateCharacterList.remove(0);
        updateCharacterList.remove(9);

        // when //
        // 변경된 내용 업데이트 및 추가, 삭제
        memberService.updateCharacterList(member, updateCharacterList);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : member.getCharacters()) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // 결과
        List<CharacterResponseDto> characterResponseDtoList = calculatedCharacterList.stream()
                .map(character -> new CharacterResponseDto().toDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator
                .comparingInt(CharacterResponseDto::getSortNumber)
                .thenComparing(Comparator.comparingDouble(CharacterResponseDto::getItemLevel).reversed())
        );

        // then //
        //업데이트 후 리스트 사이즈는 업데이트 전 리스트 사이즈보다 작다
        Assertions.assertThat(characterResponseDtoList.size()).isLessThan(beforeCharacterSize);
        //2개의 캐릭터를 삭제했기 때문에 업데이트 후 리스트 사이즈는 업데이트 전 리스트 사이즈 - 2이다.
        Assertions.assertThat(characterResponseDtoList.size()).isEqualTo(beforeCharacterSize-2);
    }

    @Test
    @DisplayName("회원 캐릭터 업데이트 성공 - 데이터 변경")
    void updateCharacterListTestUpdateCharacter() {
        // given //
        String username = "repeat2487@gmail.com";
        Member member = memberService.findMember(username);
        final int beforeCharacterSize = member.getCharacters().size();

        // 대표캐릭터와 연동된 캐릭터(api 검증)
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
                member.getCharacters().get(0).getCharacterName(), member.getApiKey(), chaos, guardian);

        // 테스트용 캐릭터 데이터 변경
        double itemLevel = 1650.0;
        updateCharacterList.get(0).setItemLevel(itemLevel);
        updateCharacterList.get(1).setItemLevel(itemLevel);

        // when //
        // 변경된 내용 업데이트 및 추가, 삭제
        memberService.updateCharacterList(member, updateCharacterList);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : member.getCharacters()) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // 결과
        List<CharacterResponseDto> characterResponseDtoList = calculatedCharacterList.stream()
                .map(character -> new CharacterResponseDto().toDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator
                .comparingInt(CharacterResponseDto::getSortNumber)
                .thenComparing(Comparator.comparingDouble(CharacterResponseDto::getItemLevel).reversed())
        );

        // then //
        //업데이트 후 리스트 사이즈는 업데이트 전 리스트 사이즈와 같다
        Assertions.assertThat(characterResponseDtoList.size()).isEqualTo(beforeCharacterSize);
        //업데이트 된 아이템레벨 확인
        Assertions.assertThat(characterResponseDtoList.get(0).getItemLevel()).isEqualTo(itemLevel);
        Assertions.assertThat(characterResponseDtoList.get(1).getItemLevel()).isEqualTo(itemLevel);
    }

    @Test
    @DisplayName("서버 분리 캐릭터 조회 테스트")
    void findCharacterListServerNameTest() {
        // given
        String username = "tjdgus9564@gmail.com";
        // username -> member 조회
        Member member = memberService.findMember(username);
        if(member.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        String serverName = member.getCharacters().get(0).getServerName();
        List<Character> characterList = characterService.findCharacterListServerName(member, serverName);
        // 결과
        List<CharacterResponseDto> characterResponseDtoList = characterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterResponseDto().toDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator
                .comparingInt(CharacterResponseDto::getSortNumber)
                .thenComparing(Comparator.comparingDouble(CharacterResponseDto::getItemLevel).reversed())
        );

        for (CharacterResponseDto characterResponseDto : characterResponseDtoList) {
            System.out.println("characterResponseDto.getCharacterName() = " + characterResponseDto.getCharacterName());
            System.out.println("characterResponseDto.getItemLevel() = " + characterResponseDto.getItemLevel());
            System.out.println("characterResponseDto.getTodoList() = " + characterResponseDto.getTodoList());
        }
    }
}