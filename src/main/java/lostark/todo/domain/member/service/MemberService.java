package lostark.todo.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.controller.dto.memberDto.LoginMemberRequest;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberResponse;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.util.market.repository.MarketRepository;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.member.infra.MemberLockManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.global.Constant.TEST_USERNAME;
import static lostark.todo.global.exhandler.ErrorMessageConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberLockManager memberLockManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final MarketRepository marketRepository;

    // 회원 - 캐릭터 조인 조회 - Test Code X
    @Transactional(readOnly = true)
    public Member get(String username) {
        return memberRepository.get(username);
    }

    @Transactional(readOnly = true)
    public Member get(Long id) {
        return memberRepository.get(id);
    }

    // 1차 회원가입
    @Transactional
    public Member createMember(String mail, String password) {
        if (memberRepository.existsByUsername(mail)) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .username(mail)
                .password(passwordEncoder.encode(password))
                .characters(new ArrayList<>())
                .authProvider("none")
                .role(Role.USER)
                .build();

        return memberRepository.save(member);
    }

    // 회원가입 후 캐릭터 추가
    @Transactional
    public void createCharacter(String username, SaveCharacterRequest request) {
        try (var ignored = memberLockManager.acquireLock(username)) {
            Member member = get(username);
            validateCreateCharacter(member);
            List<Character> characterList = createAndCalculateCharacters(request);
            member.createCharacter(characterList, request);
        }
    }

    private static void validateCreateCharacter(Member member) {
        if (!member.getCharacters().isEmpty()) {
            throw new IllegalStateException(CHARACTER_ALREADY_EXISTS);
        }
    }

    private List<Character> createAndCalculateCharacters(SaveCharacterRequest request) {
        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> characterList = lostarkCharacterApiClient.createCharacterList(
                request.getCharacterName(), request.getApiKey());

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketRepository.findLevelUpResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        return characterList.stream()
                .map(character -> character.calculateDayTodo(character, contentResource))
                .collect(Collectors.toList());
    }

    // 로그인 검증
    @Transactional
    public Member validateLogin(LoginMemberRequest request) {
        Member member = get(request.getUsername());
        if (passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new IllegalArgumentException(LOGIN_FAIL);
        }
    }

    // 대표 캐릭터 변경 - Test Code 작성완료
    @Transactional
    public void editMainCharacter(String username, String mainCharacter) {
        Member member = get(username);
        boolean characterExists = member.getCharacters().stream()
                .map(Character::getCharacterName)
                .anyMatch(characterName -> characterName.equals(mainCharacter));

        if (characterExists) {
            member.editMainCharacter(mainCharacter);
        } else {
            throw new IllegalArgumentException(MEMBER_CHARACTER_NOT_FOUND);
        }
    }

    // 유저 전환(소셜 로그인 -> 일반 로그인) - Test Code 작성완료
    @Transactional
    public void editProvider(String username, String newPassword) {
        if (username.equals(TEST_USERNAME)) {
            throw new IllegalArgumentException(TEST_MEMBER_NOT_ACCESS);
        }

        Member member = get(username);

        if (member.getAuthProvider().equals("none")) {
            throw new IllegalArgumentException(MEMBER_NOT_SOCIAL);
        }

        member.changeAuthToNone(passwordEncoder.encode(newPassword));
    }

    // 비밀번호 변경 - Test Code 작성 X
    @Transactional
    public void updatePassword(String mail, String newPassword) {
        Member member = get(mail);
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    //TODO 추후 삭제
    @Transactional
    public void createCharacterOLDER(String username, SaveCharacterRequest request, List<Character> characterList) {
        Member member = get(username);
        member.createCharacter(characterList, request);
    }

    // 회원 API KEY 수정 - Test Code 작성 X
    public void editApiKey(Member member, String apiKey) {
        member.editApiKey(apiKey);
    }

    // 가입 여부 확인 - Test Code 작성 X
    @Transactional(readOnly = true)
    public boolean existByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    // 회원 삭제 - Test Code 작성 X
    @Transactional
    public void deleteMember(String name) {
        Member member = get(name);
        memberRepository.delete(member);
    }

    // Admin 일일 가입자 수 통계 호출 - Test Code 작성 X
    @Transactional(readOnly = true)
    public List<DashboardResponse> searchMemberDashBoard(int limit) {
        return memberRepository.searchMemberDashBoard(limit);
    }

    // Admin 회원 리스트 출력 - Test Code 작성 X
    @Transactional(readOnly = true)
    public PageImpl<SearchAdminMemberResponse> searchAdminMember(SearchAdminMemberRequest request, PageRequest pageRequest) {
        return memberRepository.searchAdminMember(request, pageRequest);
    }
}
