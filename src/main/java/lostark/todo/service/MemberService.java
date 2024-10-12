package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dto.memberDto.LoginMemberRequest;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberResponse;
import lostark.todo.domain.Role;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import lostark.todo.domainV2.member.dao.MemberDao;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.Constant.TEST_USERNAME;
import static lostark.todo.constants.ErrorMessages.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberDao memberDao;

    // 회원 - 캐릭터 조인 조회 - Test Code X
    @Transactional(readOnly = true)
    public Member get(String username) {
        return memberRepository.get(username).orElseThrow(() -> new NoSuchElementException(MEMER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member get(Long id) {
        return memberRepository.get(id).orElseThrow(() -> new NoSuchElementException(MEMER_NOT_FOUND));
    }

    // 1차 회원가입 - Test Code 작성완료
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

    // 일반 로그인 - Test Code 작성 X
    @Transactional
    public Member login(LoginMemberRequest request) {
        Member member = get(request.getUsername());
        if (passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new IllegalArgumentException(LOGIN_FAIL);
        }
    }

    // 비밀번호 변경 - Test Code 작성 X
    @Transactional
    public void updatePassword(String mail, String newPassword) {
        Member member = get(mail);
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    // 회원가입 캐릭터 추가 - Test Code 작성 X
    @Transactional
    public void createCharacter(String username, SaveCharacterRequest request, List<Character> characterList) {
        Member member = memberDao.get(username);
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

    // TODO - CharacterService쪽에 있어야함
    public List<Character> editSort(String username, List<CharacterSortDto> characterSortDtoList) {
        return get(username).getCharacters().stream().peek(
                        character -> characterSortDtoList.stream()
                                .filter(characterSortDto -> character.getCharacterName().equals(characterSortDto.getCharacterName()))
                                .findFirst()
                                .ifPresent(characterSortDto -> character.setSortNumber(characterSortDto.getSortNumber())))
                .collect(Collectors.toList());

    }
}
