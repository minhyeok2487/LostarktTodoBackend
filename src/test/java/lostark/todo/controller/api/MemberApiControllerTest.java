package lostark.todo.controller.api;

import lombok.extern.slf4j.Slf4j;
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
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Slf4j
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
        String username = "ehdgmlthek@gmail.com";
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .apiKey(apiKey)
                .username(username)
                .characterName("개내동1")
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
                .apiKey(apiKey + "123")
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
                .hasMessage(characterName + " 은(는) 존재하지 않는 캐릭터 입니다.");
    }

    @Test
    @DisplayName("회원 캐릭터 업데이트 성공 - 기본")
    void updateCharacterListTest() {
        // given //
        String username = "9gjaxx@gmail.com";
        Member member = memberService.findMember(username);
        final int beforeCharacterSize = member.getCharacters().size();

        // 대표캐릭터와 연동된 캐릭터(api 검증)
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
                member.getCharacters().get(0).getCharacterName(), member.getApiKey(), chaos, guardian);

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
        Assertions.assertThat(characterResponseDtoList.size()).isEqualTo(beforeCharacterSize - 2);
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
        if (member.getCharacters().isEmpty()) {
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
    }

    @Test
    @DisplayName("중복 캐릭터")
    @Rollback(value = false)
    void findDuplicateCharacters() {
        List<Member> allByApiKeyNotNull = memberService.findAllByApiKeyNotNull();
        for (Member member : allByApiKeyNotNull) {
            boolean duplicate = false;
            List<Character> characters = member.getCharacters();
            Set<String> characterNames = new HashSet<>();
            List<Character> charactersToRemove = new ArrayList<>();

            for (Character character : characters) {
                String characterName = character.getCharacterName();

                if (characterNames.contains(characterName)) {
                    duplicate = true;
                    charactersToRemove.add(character);
                } else {
                    characterNames.add(characterName);
                }
            }

            for (Character characterToRemove : charactersToRemove) {
                characters.remove(characterToRemove);
            }

            if(duplicate) {
                log.info("중복 존재 - id: {}, username: {}", member.getId(), member.getUsername());
                log.info("삭제된 캐릭터 : {}", charactersToRemove);
            }
        }
    }

    @Test
    void test121312() {
        Member member = memberService.findMember(1149);
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        List<Character> beforeCharacterList = member.getCharacters();
        List<Character> removeList = new ArrayList<>();
        // 비교 : 캐릭터 이름, 아이템레벨, 클래스
        for (Character character : beforeCharacterList) {
            JSONObject jsonObject = lostarkCharacterService.findCharacter(character.getCharacterName(), member.getApiKey());
            if (jsonObject == null) {
                log.info("delete character name : {}", character.getCharacterName());
                //삭제 리스트에 추가
                removeList.add(character);
            } else {
                // 데이터 변경
                Character newCharacter = Character.builder()
                        .characterName(jsonObject.get("CharacterName") != null ? jsonObject.get("CharacterName").toString() : null)
                        .characterImage(jsonObject.get("CharacterImage") != null ? jsonObject.get("CharacterImage").toString() : null)
                        .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                        .itemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", "")))
                        .dayTodo(new DayTodo().createDayContent(chaos, guardian, character.getItemLevel()))
                        .build();
                characterService.updateCharacter(character, newCharacter);
            }
        }

        // 삭제
        if (!removeList.isEmpty()) {
            for (Character character : removeList) {
                characterService.deleteCharacter(beforeCharacterList, character);
            }
        }

        // 추가 리스트
        List<Character> addList = new ArrayList<>();
        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
                beforeCharacterList.get(0).getCharacterName(), member.getApiKey(), chaos, guardian);
        for (Character character : updateCharacterList) {
            boolean contain = false;
            for (Character before : beforeCharacterList) {
                if (before.getCharacterName().equals(character.getCharacterName())) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                addList.add(character);
            }
        }

        //삭제 하면서 캐릭터 닉네임 변경감지
        if (!addList.isEmpty()) {
            for (Character character : addList) {
                if (character.getCharacterImage() != null) {
                    String characterImageId = extracted(character.getCharacterImage());
                    for (Character before : removeList) {
                        if (before.getCharacterImage() != null) {
                            String beforeCharacterImageId = extracted(before.getCharacterImage());
                            if(beforeCharacterImageId.equals(characterImageId)) {
                                log.info("change characterName {} to {}", before.getCharacterName(), character.getCharacterName());
                                character = before.updateCharacter(character);
                            }
                        }
                    }
                    beforeCharacterList.add(character);
                }
            }
        }

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
                .map(character -> new CharacterResponseDto().toDtoV3(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator
                .comparingInt(CharacterResponseDto::getSortNumber)
                .thenComparing(Comparator.comparingDouble(CharacterResponseDto::getItemLevel).reversed())
        );

    }

    private static String extracted(String url) {
        // URL에서 원하는 부분을 추출
        int startIndex = url.lastIndexOf('/') + 1; // '/' 다음 인덱스부터 시작
        int endIndex = url.indexOf(".png"); // ".png" 이전까지

        return url.substring(startIndex, endIndex);

    }
}